package com.dmis.appointment.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ScheduleUpdateRequest(
        @Min(1) int maxNumber,
        @NotBlank String status
) {
}
