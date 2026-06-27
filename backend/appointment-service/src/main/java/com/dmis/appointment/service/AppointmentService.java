package com.dmis.appointment.service;

import com.dmis.appointment.client.PatientClient;
import com.dmis.appointment.model.AppointmentRecord;
import com.dmis.appointment.model.AppointmentStatus;
import com.dmis.appointment.model.DepartmentInfo;
import com.dmis.appointment.model.DoctorInfo;
import com.dmis.appointment.model.DoctorSchedule;
import com.dmis.appointment.model.ScheduleStatus;
import com.dmis.appointment.repository.AppointmentRepository;
import com.dmis.appointment.repository.DepartmentRepository;
import com.dmis.appointment.repository.DoctorRepository;
import com.dmis.appointment.repository.ScheduleRepository;
import com.dmis.appointment.web.AppointmentCreateRequest;
import com.dmis.appointment.web.AppointmentResponse;
import com.dmis.appointment.web.DepartmentRequest;
import com.dmis.appointment.web.DepartmentResponse;
import com.dmis.appointment.web.DoctorRequest;
import com.dmis.appointment.web.DoctorResponse;
import com.dmis.appointment.web.ScheduleCreateRequest;
import com.dmis.appointment.web.ScheduleResponse;
import com.dmis.appointment.web.ScheduleUpdateRequest;
import com.dmis.common.error.BusinessException;
import com.dmis.common.error.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Service
public class AppointmentService {

    private static final DateTimeFormatter APPOINTMENT_NO_DATE = DateTimeFormatter.BASIC_ISO_DATE;

    private final DepartmentRepository departmentRepository;
    private final DoctorRepository doctorRepository;
    private final ScheduleRepository scheduleRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientClient patientClient;

    public AppointmentService(
            DepartmentRepository departmentRepository,
            DoctorRepository doctorRepository,
            ScheduleRepository scheduleRepository,
            AppointmentRepository appointmentRepository,
            PatientClient patientClient
    ) {
        this.departmentRepository = departmentRepository;
        this.doctorRepository = doctorRepository;
        this.scheduleRepository = scheduleRepository;
        this.appointmentRepository = appointmentRepository;
        this.patientClient = patientClient;
    }

    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        DepartmentInfo department = new DepartmentInfo();
        department.setDeptCode(request.deptCode());
        department.setDeptName(request.deptName());
        department.setStatus(defaultStatus(request.status()));
        department.setDeleted(false);
        department.setCreatedAt(LocalDateTime.now());
        department.setUpdatedAt(LocalDateTime.now());
        return toDepartmentResponse(departmentRepository.save(department));
    }

    @Transactional(readOnly = true)
    public Page<DepartmentResponse> pageDepartments(int page, int size) {
        return departmentRepository.findByDeletedFalse(pageable(page, size)).map(this::toDepartmentResponse);
    }

    @Transactional
    public DoctorResponse createDoctor(DoctorRequest request) {
        DoctorInfo doctor = new DoctorInfo();
        doctor.setDoctorNo(request.doctorNo());
        doctor.setDoctorName(request.doctorName());
        doctor.setDeptId(request.deptId());
        doctor.setTitle(request.title());
        doctor.setPhone(request.phone());
        doctor.setStatus(defaultStatus(request.status()));
        doctor.setDeleted(false);
        doctor.setCreatedAt(LocalDateTime.now());
        doctor.setUpdatedAt(LocalDateTime.now());
        return toDoctorResponse(doctorRepository.save(doctor));
    }

    @Transactional(readOnly = true)
    public Page<DoctorResponse> pageDoctors(int page, int size) {
        return doctorRepository.findByDeletedFalse(pageable(page, size)).map(this::toDoctorResponse);
    }

    @Transactional
    public ScheduleResponse createSchedule(ScheduleCreateRequest request) {
        DoctorSchedule schedule = new DoctorSchedule();
        schedule.setDoctorId(request.doctorId());
        schedule.setDeptId(request.deptId());
        schedule.setScheduleDate(request.scheduleDate());
        schedule.setTimeSlot(request.timeSlot());
        schedule.setMaxNumber(request.maxNumber());
        schedule.setCurrentNumber(0);
        schedule.setStatus(ScheduleStatus.OPEN);
        schedule.setDeleted(false);
        schedule.setCreatedAt(LocalDateTime.now());
        schedule.setUpdatedAt(LocalDateTime.now());
        return toScheduleResponse(scheduleRepository.save(schedule));
    }

    @Transactional(readOnly = true)
    public Page<ScheduleResponse> pageSchedules(Long doctorId, LocalDate scheduleDate, int page, int size) {
        Pageable pageable = pageable(page, size);
        Page<DoctorSchedule> schedules = doctorId != null && scheduleDate != null
                ? scheduleRepository.findByDeletedFalseAndDoctorIdAndScheduleDate(doctorId, scheduleDate, pageable)
                : scheduleRepository.findByDeletedFalse(pageable);
        return schedules.map(this::toScheduleResponse);
    }

    @Transactional(readOnly = true)
    public ScheduleResponse getSchedule(Long id) {
        return toScheduleResponse(loadSchedule(id));
    }

    @Transactional
    public ScheduleResponse updateSchedule(Long id, ScheduleUpdateRequest request) {
        DoctorSchedule schedule = loadSchedule(id);
        if (request.maxNumber() < schedule.getCurrentNumber()) {
            throw new BusinessException(ErrorCode.CONFLICT, "Max number cannot be smaller than current bookings");
        }
        schedule.setMaxNumber(request.maxNumber());
        schedule.setStatus(parseScheduleStatus(request.status()));
        schedule.setUpdatedAt(LocalDateTime.now());
        return toScheduleResponse(scheduleRepository.save(schedule));
    }

    @Transactional
    public void deleteSchedule(Long id) {
        DoctorSchedule schedule = loadSchedule(id);
        if (schedule.getCurrentNumber() > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "Cannot delete schedule with active bookings");
        }
        schedule.setDeleted(true);
        schedule.setUpdatedAt(LocalDateTime.now());
        scheduleRepository.save(schedule);
    }

    @Transactional
    public AppointmentResponse book(AppointmentCreateRequest request) {
        DoctorSchedule schedule = loadSchedule(request.scheduleId());
        if (schedule.getStatus() != ScheduleStatus.OPEN) {
            throw new BusinessException(ErrorCode.CONFLICT, "Schedule is not open");
        }
        if (schedule.getCurrentNumber() >= schedule.getMaxNumber()) {
            throw new BusinessException(ErrorCode.CONFLICT, "Schedule quota is full");
        }

        patientClient.getPatientSummary(request.patientId());

        schedule.setCurrentNumber(schedule.getCurrentNumber() + 1);
        schedule.setUpdatedAt(LocalDateTime.now());
        scheduleRepository.save(schedule);

        AppointmentRecord appointment = new AppointmentRecord();
        appointment.setAppointmentNo(generateAppointmentNo());
        appointment.setPatientId(request.patientId());
        appointment.setDoctorId(request.doctorId());
        appointment.setDeptId(request.deptId());
        appointment.setScheduleId(request.scheduleId());
        appointment.setAppointmentDate(request.appointmentDate());
        appointment.setTimeSlot(request.timeSlot());
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setSource(request.source());
        appointment.setDeleted(false);
        appointment.setCreatedAt(LocalDateTime.now());
        appointment.setUpdatedAt(LocalDateTime.now());
        return toAppointmentResponse(appointmentRepository.save(appointment));
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponse> pageAppointments(Long patientId, int page, int size) {
        Pageable pageable = pageable(page, size);
        Page<AppointmentRecord> records = patientId == null
                ? appointmentRepository.findByDeletedFalse(pageable)
                : appointmentRepository.findByDeletedFalseAndPatientId(patientId, pageable);
        return records.map(this::toAppointmentResponse);
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getAppointment(Long id) {
        return toAppointmentResponse(loadAppointment(id));
    }

    @Transactional
    public AppointmentResponse cancel(Long id) {
        AppointmentRecord appointment = loadAppointment(id);
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            return toAppointmentResponse(appointment);
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setUpdatedAt(LocalDateTime.now());
        appointmentRepository.save(appointment);

        DoctorSchedule schedule = loadSchedule(appointment.getScheduleId());
        schedule.setCurrentNumber(Math.max(0, schedule.getCurrentNumber() - 1));
        schedule.setUpdatedAt(LocalDateTime.now());
        scheduleRepository.save(schedule);

        return toAppointmentResponse(appointment);
    }

    private DoctorSchedule loadSchedule(Long id) {
        return scheduleRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Schedule not found"));
    }

    private AppointmentRecord loadAppointment(Long id) {
        return appointmentRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Appointment not found"));
    }

    private ScheduleStatus parseScheduleStatus(String status) {
        try {
            return ScheduleStatus.valueOf(status.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Unsupported schedule status");
        }
    }

    private Pageable pageable(int page, int size) {
        return PageRequest.of(Math.max(page, 0), Math.max(size, 1));
    }

    private String defaultStatus(String status) {
        return status == null || status.isBlank() ? "ENABLED" : status;
    }

    private String generateAppointmentNo() {
        return "A" + LocalDate.now().format(APPOINTMENT_NO_DATE) + UUID.randomUUID().toString().substring(0, 6).toUpperCase(Locale.ROOT);
    }

    private DepartmentResponse toDepartmentResponse(DepartmentInfo department) {
        return new DepartmentResponse(department.getId(), department.getDeptCode(), department.getDeptName(), department.getStatus());
    }

    private DoctorResponse toDoctorResponse(DoctorInfo doctor) {
        return new DoctorResponse(doctor.getId(), doctor.getDoctorNo(), doctor.getDoctorName(), doctor.getDeptId(), doctor.getTitle(), doctor.getPhone(), doctor.getStatus());
    }

    private ScheduleResponse toScheduleResponse(DoctorSchedule schedule) {
        return new ScheduleResponse(
                schedule.getId(),
                schedule.getDoctorId(),
                schedule.getDeptId(),
                schedule.getScheduleDate(),
                schedule.getTimeSlot(),
                schedule.getMaxNumber(),
                schedule.getCurrentNumber(),
                schedule.getStatus().name()
        );
    }

    private AppointmentResponse toAppointmentResponse(AppointmentRecord appointment) {
        return new AppointmentResponse(
                appointment.getId(),
                appointment.getAppointmentNo(),
                appointment.getPatientId(),
                appointment.getDoctorId(),
                appointment.getDeptId(),
                appointment.getScheduleId(),
                appointment.getAppointmentDate(),
                appointment.getTimeSlot(),
                appointment.getStatus().name(),
                appointment.getSource()
        );
    }
}
