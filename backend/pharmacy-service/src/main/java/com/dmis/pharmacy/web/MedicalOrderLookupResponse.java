package com.dmis.pharmacy.web;

public record MedicalOrderLookupResponse(
        Long id,
        String orderNo,
        Long patientId,
        Long recordId,
        Long doctorId,
        String orderType,
        String orderContent,
        String dosage,
        String frequency,
        Integer durationDays,
        String status
) {
}
