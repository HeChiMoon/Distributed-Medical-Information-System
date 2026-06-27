package com.dmis.record.web;

public record MedicalRecordResponse(
        Long id,
        String recordNo,
        Long patientId,
        Long appointmentId,
        Long doctorId,
        String chiefComplaint,
        String presentIllness,
        String pastHistory,
        String physicalExam,
        String diagnosis,
        String advice,
        String recordStatus
) {
}
