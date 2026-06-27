package com.dmis.admin.web;

import jakarta.validation.constraints.NotBlank;

public record DictTypeRequest(
        @NotBlank String dictCode,
        @NotBlank String dictName,
        String status
) {
}
