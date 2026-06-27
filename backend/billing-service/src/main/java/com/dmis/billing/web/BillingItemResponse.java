package com.dmis.billing.web;

import java.math.BigDecimal;

public record BillingItemResponse(
        Long id,
        Long billId,
        String itemType,
        String itemName,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal amount
) {
}
