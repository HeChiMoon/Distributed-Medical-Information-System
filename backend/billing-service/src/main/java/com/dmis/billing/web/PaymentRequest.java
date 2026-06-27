package com.dmis.billing.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PaymentRequest(
        @NotBlank String paymentMethod,
        @NotNull @Positive BigDecimal paymentAmount
) {
}
