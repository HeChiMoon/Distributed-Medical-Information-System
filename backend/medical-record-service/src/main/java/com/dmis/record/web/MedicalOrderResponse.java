package com.dmis.record.web;

public record MedicalOrderResponse(
        Long id,
        String orderNo,
        Long recordId,
        Long patientId,
        Long doctorId,
        String orderType,
        String orderContent,
        String dosage,
        String frequency,
        Integer durationDays,
        String status
) {
}
