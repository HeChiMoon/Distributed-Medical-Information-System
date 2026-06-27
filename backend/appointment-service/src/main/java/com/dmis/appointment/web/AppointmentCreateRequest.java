package com.dmis.appointment.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record AppointmentCreateRequest(
        @NotNull Long patientId,
        @NotNull Long doctorId,
        @NotNull Long deptId,
        @NotNull Long scheduleId,
        @NotNull LocalDate appointmentDate,
        @NotBlank String timeSlot,
        @NotBlank String source
) {
}
