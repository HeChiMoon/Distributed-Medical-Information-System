package com.dmis.billing.web;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record BillingResponse(
        Long id,
        String billNo,
        Long patientId,
        Long appointmentId,
        Long recordId,
        BigDecimal totalAmount,
        BigDecimal paidAmount,
        String billStatus,
        String paymentStatus,
        LocalDateTime billingTime
) {
}
