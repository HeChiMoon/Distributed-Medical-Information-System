package com.dmis.pharmacy.web;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record DispenseRequest(
        Long recordId,
        @NotNull Long patientId,
        @NotNull Long orderId,
        @NotNull Long drugId,
        @NotNull @Positive Integer quantity,
        String pharmacistName
) {
}
