package com.dmis.billing.web;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record BillingCreateRequest(
        @NotNull Long patientId,
        Long appointmentId,
        Long recordId,
        @Valid @NotEmpty List<BillingItemRequest> items
) {
}
