package com.dmis.pharmacy.web;

import java.time.LocalDate;

public record InventoryResponse(
        Long id,
        Long drugId,
        String batchNo,
        Integer quantity,
        Integer lockedQuantity,
        LocalDate productionDate,
        LocalDate expireDate,
        String warehouseLocation
) {
}
