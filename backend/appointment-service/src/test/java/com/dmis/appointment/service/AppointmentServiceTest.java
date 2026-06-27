package com.dmis.appointment.service;

import com.dmis.appointment.client.PatientClient;
import com.dmis.appointment.model.AppointmentRecord;
import com.dmis.appointment.model.AppointmentStatus;
import com.dmis.appointment.model.DoctorSchedule;
import com.dmis.appointment.model.ScheduleStatus;
import com.dmis.appointment.repository.AppointmentRepository;
import com.dmis.appointment.repository.DepartmentRepository;
import com.dmis.appointment.repository.DoctorRepository;
import com.dmis.appointment.repository.ScheduleRepository;
import com.dmis.appointment.web.AppointmentCreateRequest;
import com.dmis.appointment.web.AppointmentResponse;
import com.dmis.appointment.web.ScheduleCreateRequest;
import com.dmis.appointment.web.ScheduleResponse;
import com.dmis.common.api.PatientSummary;
import com.dmis.common.error.BusinessException;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AppointmentServiceTest {

    private final DepartmentRepository departmentRepository = mock(DepartmentRepository.class);
    private final DoctorRepository doctorRepository = mock(DoctorRepository.class);
    private final ScheduleRepository scheduleRepository = mock(ScheduleRepository.class);
    private final AppointmentRepository appointmentRepository = mock(AppointmentRepository.class);
    private final PatientClient patientClient = mock(PatientClient.class);
    private final AppointmentService service = new AppointmentService(
            departmentRepository,
            doctorRepository,
            scheduleRepository,
            appointmentRepository,
            patientClient
    );

    @Test
    void createScheduleStartsOpenWithZeroBookings() {
        when(scheduleRepository.save(any(DoctorSchedule.class))).thenAnswer(invocation -> {
            DoctorSchedule schedule = invocation.getArgument(0);
            schedule.setId(10L);
            return schedule;
        });

        ScheduleResponse response = service.createSchedule(new ScheduleCreateRequest(
                1L,
                2L,
                LocalDate.of(2026, 6, 28),
                "AM",
                20
        ));

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.currentNumber()).isZero();
        assertThat(response.status()).isEqualTo(ScheduleStatus.OPEN.name());
    }

    @Test
    void bookAppointmentCallsPatientServiceAndOccupiesScheduleQuota() {
        DoctorSchedule schedule = schedule(10L, 1L, 2L, 20, 3);
        when(scheduleRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(schedule));
        when(patientClient.getPatientSummary(100L)).thenReturn(new PatientSummary(100L, "P202606270001", "Alice", "13800000000"));
        when(appointmentRepository.save(any(AppointmentRecord.class))).thenAnswer(invocation -> {
            AppointmentRecord appointment = invocation.getArgument(0);
            appointment.setId(99L);
            return appointment;
        });

        AppointmentResponse response = service.book(new AppointmentCreateRequest(
                100L,
                1L,
                2L,
                10L,
                LocalDate.of(2026, 6, 28),
                "AM",
                "WEB"
        ));

        assertThat(response.id()).isEqualTo(99L);
        assertThat(response.patientId()).isEqualTo(100L);
        assertThat(response.status()).isEqualTo(AppointmentStatus.PENDING.name());
        assertThat(schedule.getCurrentNumber()).isEqualTo(4);
        verify(patientClient).getPatientSummary(100L);
        verify(scheduleRepository).save(schedule);
    }

    @Test
    void bookAppointmentRejectsFullSchedule() {
        DoctorSchedule schedule = schedule(10L, 1L, 2L, 3, 3);
        when(scheduleRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(schedule));

        assertThatThrownBy(() -> service.book(new AppointmentCreateRequest(
                100L,
                1L,
                2L,
                10L,
                LocalDate.of(2026, 6, 28),
                "AM",
                "WEB"
        ))).isInstanceOf(BusinessException.class)
                .hasMessageContaining("Schedule quota is full");
    }

    @Test
    void cancelAppointmentMarksCancelledAndReleasesQuota() {
        DoctorSchedule schedule = schedule(10L, 1L, 2L, 20, 3);
        AppointmentRecord appointment = appointment(99L, 100L, 10L);
        when(appointmentRepository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.of(appointment));
        when(scheduleRepository.findByIdAndDeletedFalse(10L)).thenReturn(Optional.of(schedule));

        AppointmentResponse response = service.cancel(99L);

        assertThat(response.status()).isEqualTo(AppointmentStatus.CANCELLED.name());
        assertThat(schedule.getCurrentNumber()).isEqualTo(2);
        verify(appointmentRepository).save(appointment);
        verify(scheduleRepository).save(schedule);
    }

    private DoctorSchedule schedule(Long id, Long doctorId, Long deptId, int maxNumber, int currentNumber) {
        DoctorSchedule schedule = new DoctorSchedule();
        schedule.setId(id);
        schedule.setDoctorId(doctorId);
        schedule.setDeptId(deptId);
        schedule.setScheduleDate(LocalDate.of(2026, 6, 28));
        schedule.setTimeSlot("AM");
        schedule.setMaxNumber(maxNumber);
        schedule.setCurrentNumber(currentNumber);
        schedule.setStatus(ScheduleStatus.OPEN);
        schedule.setDeleted(false);
        return schedule;
    }

    private AppointmentRecord appointment(Long id, Long patientId, Long scheduleId) {
        AppointmentRecord appointment = new AppointmentRecord();
        appointment.setId(id);
        appointment.setAppointmentNo("A202606280001");
        appointment.setPatientId(patientId);
        appointment.setDoctorId(1L);
        appointment.setDeptId(2L);
        appointment.setScheduleId(scheduleId);
        appointment.setAppointmentDate(LocalDate.of(2026, 6, 28));
        appointment.setTimeSlot("AM");
        appointment.setStatus(AppointmentStatus.PENDING);
        appointment.setSource("WEB");
        appointment.setDeleted(false);
        return appointment;
    }
}
