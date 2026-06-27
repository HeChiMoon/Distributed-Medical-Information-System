package com.dmis.record.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MedicalRecordCreateRequest(
        @NotNull Long patientId,
        Long appointmentId,
        @NotNull Long doctorId,
        @NotBlank @Size(max = 500) String chiefComplaint,
        @Size(max = 1000) String presentIllness,
        @Size(max = 1000) String pastHistory,
        @Size(max = 1000) String physicalExam,
        @NotBlank @Size(max = 500) String diagnosis,
        @Size(max = 500) String advice
) {
}
