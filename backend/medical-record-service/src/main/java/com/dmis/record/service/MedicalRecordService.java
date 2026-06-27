package com.dmis.record.service;

import com.dmis.common.api.ApiResponse;
import com.dmis.common.error.BusinessException;
import com.dmis.common.error.ErrorCode;
import com.dmis.record.client.AppointmentClient;
import com.dmis.record.client.PatientClient;
import com.dmis.record.model.MedicalOrder;
import com.dmis.record.model.MedicalRecord;
import com.dmis.record.model.OrderStatus;
import com.dmis.record.model.RecordStatus;
import com.dmis.record.repository.MedicalOrderRepository;
import com.dmis.record.repository.MedicalRecordRepository;
import com.dmis.record.web.*;
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
public class MedicalRecordService {

    private static final DateTimeFormatter NO_DATE = DateTimeFormatter.BASIC_ISO_DATE;

    private final MedicalRecordRepository recordRepository;
    private final MedicalOrderRepository orderRepository;
    private final PatientClient patientClient;
    private final AppointmentClient appointmentClient;

    public MedicalRecordService(
            MedicalRecordRepository recordRepository,
            MedicalOrderRepository orderRepository,
            PatientClient patientClient,
            AppointmentClient appointmentClient
    ) {
        this.recordRepository = recordRepository;
        this.orderRepository = orderRepository;
        this.patientClient = patientClient;
        this.appointmentClient = appointmentClient;
    }

    @Transactional
    public MedicalRecordResponse createRecord(MedicalRecordCreateRequest request) {
        patientClient.getPatientSummary(request.patientId());
        if (request.appointmentId() != null) {
            ApiResponse<AppointmentLookupResponse> appointment = appointmentClient.getAppointment(request.appointmentId());
            if (appointment.data() != null && !request.patientId().equals(appointment.data().patientId())) {
                throw new BusinessException(ErrorCode.CONFLICT, "Appointment patient does not match record patient");
            }
        }

        MedicalRecord record = new MedicalRecord();
        record.recordNo = generateNo("R");
        record.patientId = request.patientId();
        record.appointmentId = request.appointmentId();
        record.doctorId = request.doctorId();
        record.chiefComplaint = request.chiefComplaint();
        record.presentIllness = request.presentIllness();
        record.pastHistory = request.pastHistory();
        record.physicalExam = request.physicalExam();
        record.diagnosis = request.diagnosis();
        record.advice = request.advice();
        record.recordStatus = RecordStatus.DRAFT;
        record.deleted = false;
        record.createdAt = LocalDateTime.now();
        record.updatedAt = LocalDateTime.now();
        return toRecordResponse(recordRepository.save(record));
    }

    @Transactional(readOnly = true)
    public Page<MedicalRecordResponse> pageRecords(Long patientId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        Page<MedicalRecord> records = patientId == null
                ? recordRepository.findByDeletedFalse(pageable)
                : recordRepository.findByDeletedFalseAndPatientId(patientId, pageable);
        return records.map(this::toRecordResponse);
    }

    @Transactional(readOnly = true)
    public MedicalRecordResponse getRecord(Long id) {
        return toRecordResponse(loadRecord(id));
    }

    @Transactional
    public MedicalRecordResponse updateRecord(Long id, MedicalRecordUpdateRequest request) {
        MedicalRecord record = loadRecord(id);
        record.chiefComplaint = request.chiefComplaint();
        record.presentIllness = request.presentIllness();
        record.pastHistory = request.pastHistory();
        record.physicalExam = request.physicalExam();
        record.diagnosis = request.diagnosis();
        record.advice = request.advice();
        record.recordStatus = parseRecordStatus(request.recordStatus());
        record.updatedAt = LocalDateTime.now();
        return toRecordResponse(recordRepository.save(record));
    }

    @Transactional
    public void deleteRecord(Long id) {
        MedicalRecord record = loadRecord(id);
        record.deleted = true;
        record.updatedAt = LocalDateTime.now();
        recordRepository.save(record);
    }

    @Transactional
    public MedicalOrderResponse createOrder(MedicalOrderCreateRequest request) {
        MedicalRecord record = loadRecord(request.recordId());
        MedicalOrder order = new MedicalOrder();
        order.orderNo = generateNo("O");
        order.recordId = record.id;
        order.patientId = record.patientId;
        order.doctorId = record.doctorId;
        order.orderType = request.orderType();
        order.orderContent = request.orderContent();
        order.dosage = request.dosage();
        order.frequency = request.frequency();
        order.durationDays = request.durationDays();
        order.status = OrderStatus.ACTIVE;
        order.deleted = false;
        order.createdAt = LocalDateTime.now();
        order.updatedAt = LocalDateTime.now();
        return toOrderResponse(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public Page<MedicalOrderResponse> pageOrders(Long recordId, Long patientId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        Page<MedicalOrder> orders = recordId != null
                ? orderRepository.findByDeletedFalseAndRecordId(recordId, pageable)
                : orderRepository.findByDeletedFalseAndPatientId(patientId, pageable);
        return orders.map(this::toOrderResponse);
    }

    @Transactional(readOnly = true)
    public MedicalOrderResponse getOrder(Long id) {
        return toOrderResponse(loadOrder(id));
    }

    @Transactional
    public MedicalOrderResponse stopOrder(Long id) {
        MedicalOrder order = loadOrder(id);
        order.status = OrderStatus.STOPPED;
        order.updatedAt = LocalDateTime.now();
        return toOrderResponse(orderRepository.save(order));
    }

    private MedicalRecord loadRecord(Long id) {
        return recordRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Medical record not found"));
    }

    private MedicalOrder loadOrder(Long id) {
        return orderRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Medical order not found"));
    }

    private RecordStatus parseRecordStatus(String status) {
        try {
            return RecordStatus.valueOf(status.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Unsupported record status");
        }
    }

    private String generateNo(String prefix) {
        return prefix + LocalDate.now().format(NO_DATE) + UUID.randomUUID().toString().substring(0, 6).toUpperCase(Locale.ROOT);
    }

    private MedicalRecordResponse toRecordResponse(MedicalRecord record) {
        return new MedicalRecordResponse(
                record.id,
                record.recordNo,
                record.patientId,
                record.appointmentId,
                record.doctorId,
                record.chiefComplaint,
                record.presentIllness,
                record.pastHistory,
                record.physicalExam,
                record.diagnosis,
                record.advice,
                record.recordStatus.name()
        );
    }

    private MedicalOrderResponse toOrderResponse(MedicalOrder order) {
        return new MedicalOrderResponse(
                order.id,
                order.orderNo,
                order.recordId,
                order.patientId,
                order.doctorId,
                order.orderType,
                order.orderContent,
                order.dosage,
                order.frequency,
                order.durationDays,
                order.status.name()
        );
    }
}
