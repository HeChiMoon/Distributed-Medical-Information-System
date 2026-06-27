package com.dmis.billing.web;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentResponse(
        Long id,
        Long billId,
        String paymentNo,
        String paymentMethod,
        BigDecimal paymentAmount,
        LocalDateTime paymentTime,
        String paymentStatus
) {
}
