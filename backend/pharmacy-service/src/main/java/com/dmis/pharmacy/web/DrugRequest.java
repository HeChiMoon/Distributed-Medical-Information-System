package com.dmis.pharmacy.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record DrugRequest(
        @NotBlank String drugCode,
        @NotBlank String drugName,
        String specification,
        String unit,
        String manufacturer,
        @NotNull @PositiveOrZero BigDecimal salePrice,
        @NotNull @PositiveOrZero Integer stockWarningLine,
        String status
) {
}
