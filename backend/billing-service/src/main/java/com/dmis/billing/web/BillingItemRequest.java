package com.dmis.billing.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record BillingItemRequest(
        @NotBlank String itemType,
        @NotBlank String itemName,
        @NotNull @Positive BigDecimal unitPrice,
        @NotNull @Positive Integer quantity
) {
}
