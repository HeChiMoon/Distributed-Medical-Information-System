package com.dmis.admin.web;

import java.time.LocalDateTime;

public record OperationLogResponse(
        Long id,
        String moduleName,
        String operationType,
        Long operatorId,
        String operatorName,
        String requestUri,
        String requestMethod,
        String operationResult,
        LocalDateTime operationTime
) {
}
