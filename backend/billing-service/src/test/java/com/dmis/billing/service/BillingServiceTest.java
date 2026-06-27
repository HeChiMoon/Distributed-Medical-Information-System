package com.dmis.billing.service;

import com.dmis.billing.client.PatientClient;
import com.dmis.billing.model.BillingItem;
import com.dmis.billing.model.BillingRecord;
import com.dmis.billing.model.PaymentRecord;
import com.dmis.billing.repository.BillingItemRepository;
import com.dmis.billing.repository.BillingRepository;
import com.dmis.billing.repository.PaymentRepository;
import com.dmis.billing.web.BillingCreateRequest;
import com.dmis.billing.web.BillingItemRequest;
import com.dmis.billing.web.BillingResponse;
import com.dmis.billing.web.PaymentRequest;
import com.dmis.billing.web.PaymentResponse;
import com.dmis.common.api.PatientSummary;
import com.dmis.common.error.BusinessException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BillingServiceTest {

    private final BillingRepository billingRepository = mock(BillingRepository.class);
    private final BillingItemRepository itemRepository = mock(BillingItemRepository.class);
    private final PaymentRepository paymentRepository = mock(PaymentRepository.class);
    private final PatientClient patientClient = mock(PatientClient.class);
    private final BillingService service = new BillingService(billingRepository, itemRepository, paymentRepository, patientClient);

    @Test
    void createBillCalculatesTotalAndStoresItems() {
        when(patientClient.getPatientSummary(9L)).thenReturn(new PatientSummary(9L, "P001", "Alice", "13800000000"));
        when(billingRepository.save(any(BillingRecord.class))).thenAnswer(invocation -> {
            BillingRecord record = invocation.getArgument(0);
            record.id = 6L;
            return record;
        });

        BillingCreateRequest request = new BillingCreateRequest(
                9L,
                3L,
                4L,
                List.of(
                        new BillingItemRequest("REGISTRATION", "Expert registration", new BigDecimal("30.00"), 1),
                        new BillingItemRequest("DRUG", "Ibuprofen", new BigDecimal("12.50"), 2)
                )
        );

        BillingResponse response = service.createBill(request);

        assertThat(response.id()).isEqualTo(6L);
        assertThat(response.totalAmount()).isEqualByComparingTo("55.00");
        assertThat(response.paymentStatus()).isEqualTo("UNPAID");
        verify(itemRepository).saveAll(any());
    }

    @Test
    void payPartiallyKeepsPartialStatus() {
        BillingRecord bill = bill(new BigDecimal("100.00"), BigDecimal.ZERO, "VALID");
        when(billingRepository.findByIdAndDeletedFalse(6L)).thenReturn(Optional.of(bill));
        when(paymentRepository.save(any(PaymentRecord.class))).thenAnswer(invocation -> {
            PaymentRecord payment = invocation.getArgument(0);
            payment.id = 10L;
            return payment;
        });

        PaymentResponse response = service.pay(6L, new PaymentRequest("WECHAT", new BigDecimal("40.00")));

        assertThat(response.id()).isEqualTo(10L);
        assertThat(bill.paidAmount).isEqualByComparingTo("40.00");
        assertThat(bill.paymentStatus).isEqualTo("PARTIAL");
        verify(billingRepository).save(bill);
    }

    @Test
    void payFullyMarksBillPaid() {
        BillingRecord bill = bill(new BigDecimal("100.00"), new BigDecimal("40.00"), "VALID");
        when(billingRepository.findByIdAndDeletedFalse(6L)).thenReturn(Optional.of(bill));
        when(paymentRepository.save(any(PaymentRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.pay(6L, new PaymentRequest("CASH", new BigDecimal("60.00")));

        assertThat(bill.paidAmount).isEqualByComparingTo("100.00");
        assertThat(bill.paymentStatus).isEqualTo("PAID");
    }

    @Test
    void payRejectsCancelledBill() {
        when(billingRepository.findByIdAndDeletedFalse(6L)).thenReturn(Optional.of(bill(new BigDecimal("100.00"), BigDecimal.ZERO, "CANCELLED")));

        assertThatThrownBy(() -> service.pay(6L, new PaymentRequest("CASH", new BigDecimal("10.00"))))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("cancelled bill");
    }

    private BillingRecord bill(BigDecimal total, BigDecimal paid, String billStatus) {
        BillingRecord bill = new BillingRecord();
        bill.id = 6L;
        bill.billNo = "B001";
        bill.patientId = 9L;
        bill.totalAmount = total;
        bill.paidAmount = paid;
        bill.billStatus = billStatus;
        bill.paymentStatus = paid.compareTo(BigDecimal.ZERO) == 0 ? "UNPAID" : "PARTIAL";
        bill.deleted = false;
        return bill;
    }
}
