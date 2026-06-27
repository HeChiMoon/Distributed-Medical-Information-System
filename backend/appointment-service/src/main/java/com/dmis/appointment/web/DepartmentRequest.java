package com.dmis.appointment.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DepartmentRequest(
        @NotBlank @Size(max = 64) String deptCode,
        @NotBlank @Size(max = 128) String deptName,
        @Size(max = 20) String status
) {
}
