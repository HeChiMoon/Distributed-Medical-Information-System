package com.dmis.pharmacy.web;

import java.time.LocalDateTime;

public record DispenseResponse(
        Long id,
        String dispenseNo,
        Long patientId,
        Long recordId,
        Long orderId,
        Long drugId,
        Integer quantity,
        String dispenseStatus,
        LocalDateTime dispenseTime,
        String pharmacistName
) {
}
