package com.dmis.appointment.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DoctorRequest(
        @NotBlank @Size(max = 64) String doctorNo,
        @NotBlank @Size(max = 64) String doctorName,
        @NotNull Long deptId,
        @Size(max = 64) String title,
        @Size(max = 32) String phone,
        @Size(max = 20) String status
) {
}
