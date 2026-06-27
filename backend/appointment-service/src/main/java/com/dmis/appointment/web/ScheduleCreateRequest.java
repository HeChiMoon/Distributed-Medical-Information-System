package com.dmis.appointment.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ScheduleCreateRequest(
        @NotNull Long doctorId,
        @NotNull Long deptId,
        @NotNull LocalDate scheduleDate,
        @NotBlank String timeSlot,
        @Min(1) int maxNumber
) {
}
