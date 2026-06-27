package com.dmis.pharmacy.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record InventoryInboundRequest(
        @NotNull Long drugId,
        @NotBlank String batchNo,
        @NotNull @Positive Integer quantity,
        LocalDate productionDate,
        LocalDate expireDate,
        String warehouseLocation
) {
}
