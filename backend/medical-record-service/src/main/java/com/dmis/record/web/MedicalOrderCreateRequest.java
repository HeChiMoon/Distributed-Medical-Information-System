package com.dmis.record.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MedicalOrderCreateRequest(
        @NotNull Long recordId,
        @NotBlank @Size(max = 32) String orderType,
        @NotBlank @Size(max = 500) String orderContent,
        @Size(max = 128) String dosage,
        @Size(max = 128) String frequency,
        Integer durationDays
) {
}
