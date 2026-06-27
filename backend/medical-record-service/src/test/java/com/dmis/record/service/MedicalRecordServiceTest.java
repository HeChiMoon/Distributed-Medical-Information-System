package com.dmis.record.service;

import com.dmis.common.api.ApiResponse;
import com.dmis.common.api.PatientSummary;
import com.dmis.common.error.BusinessException;
import com.dmis.record.client.AppointmentClient;
import com.dmis.record.client.PatientClient;
import com.dmis.record.model.MedicalOrder;
import com.dmis.record.model.MedicalRecord;
import com.dmis.record.model.OrderStatus;
import com.dmis.record.repository.MedicalOrderRepository;
import com.dmis.record.repository.MedicalRecordRepository;
import com.dmis.record.web.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MedicalRecordServiceTest {

    private final MedicalRecordRepository recordRepository = mock(MedicalRecordRepository.class);
    private final MedicalOrderRepository orderRepository = mock(MedicalOrderRepository.class);
    private final PatientClient patientClient = mock(PatientClient.class);
    private final AppointmentClient appointmentClient = mock(AppointmentClient.class);
    private final MedicalRecordService service = new MedicalRecordService(recordRepository, orderRepository, patientClient, appointmentClient);

    @Test
    void createRecordValidatesPatientAndAppointment() {
        when(patientClient.getPatientSummary(1L)).thenReturn(new PatientSummary(1L, "P1", "Alice", "138"));
        when(appointmentClient.getAppointment(10L)).thenReturn(ApiResponse.ok(new AppointmentLookupResponse(10L, "A1", 1L, 2L, 3L, 4L, LocalDate.now(), "AM", "PENDING", "WEB")));
        when(recordRepository.save(any(MedicalRecord.class))).thenAnswer(invocation -> {
            MedicalRecord record = invocation.getArgument(0);
            record.id = 99L;
            return record;
        });

        MedicalRecordResponse response = service.createRecord(new MedicalRecordCreateRequest(1L, 10L, 2L, "Headache", "Two days", "", "", "Cold", "Rest"));

        assertThat(response.id()).isEqualTo(99L);
        assertThat(response.recordNo()).startsWith("R");
        verify(patientClient).getPatientSummary(1L);
        verify(appointmentClient).getAppointment(10L);
    }

    @Test
    void createRecordRejectsAppointmentPatientMismatch() {
        when(patientClient.getPatientSummary(1L)).thenReturn(new PatientSummary(1L, "P1", "Alice", "138"));
        when(appointmentClient.getAppointment(10L)).thenReturn(ApiResponse.ok(new AppointmentLookupResponse(10L, "A1", 2L, 2L, 3L, 4L, LocalDate.now(), "AM", "PENDING", "WEB")));

        assertThatThrownBy(() -> service.createRecord(new MedicalRecordCreateRequest(1L, 10L, 2L, "Headache", "", "", "", "Cold", "")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Appointment patient");
    }

    @Test
    void createOrderCopiesPatientAndDoctorFromRecord() {
        MedicalRecord record = record();
        when(recordRepository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.of(record));
        when(orderRepository.save(any(MedicalOrder.class))).thenAnswer(invocation -> {
            MedicalOrder order = invocation.getArgument(0);
            order.id = 8L;
            return order;
        });

        MedicalOrderResponse response = service.createOrder(new MedicalOrderCreateRequest(99L, "DRUG", "Ibuprofen", "1 tablet", "BID", 3));

        assertThat(response.patientId()).isEqualTo(1L);
        assertThat(response.doctorId()).isEqualTo(2L);
        assertThat(response.status()).isEqualTo(OrderStatus.ACTIVE.name());
    }

    @Test
    void stopOrderMarksOrderStopped() {
        MedicalOrder order = new MedicalOrder();
        order.id = 8L;
        order.orderNo = "O1";
        order.recordId = 99L;
        order.patientId = 1L;
        order.doctorId = 2L;
        order.orderType = "DRUG";
        order.orderContent = "Ibuprofen";
        order.status = OrderStatus.ACTIVE;
        order.deleted = false;
        when(orderRepository.findByIdAndDeletedFalse(8L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        MedicalOrderResponse response = service.stopOrder(8L);

        assertThat(response.status()).isEqualTo(OrderStatus.STOPPED.name());
        verify(orderRepository).save(order);
    }

    private MedicalRecord record() {
        MedicalRecord record = new MedicalRecord();
        record.id = 99L;
        record.recordNo = "R1";
        record.patientId = 1L;
        record.appointmentId = 10L;
        record.doctorId = 2L;
        record.chiefComplaint = "Headache";
        record.diagnosis = "Cold";
        record.deleted = false;
        return record;
    }
}
