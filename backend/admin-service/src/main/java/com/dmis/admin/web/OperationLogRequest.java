package com.dmis.admin.web;

import jakarta.validation.constraints.NotBlank;

public record OperationLogRequest(
        @NotBlank String moduleName,
        @NotBlank String operationType,
        Long operatorId,
        String operatorName,
        String requestUri,
        String requestMethod,
        String operationResult
) {
}
