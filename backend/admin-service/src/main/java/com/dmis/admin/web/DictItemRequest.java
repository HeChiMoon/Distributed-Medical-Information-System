package com.dmis.admin.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DictItemRequest(
        @NotNull Long dictTypeId,
        @NotBlank String itemCode,
        @NotBlank String itemName,
        Integer sortNo,
        String status
) {
}
