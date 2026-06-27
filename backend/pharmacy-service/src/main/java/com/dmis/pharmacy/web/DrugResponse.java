package com.dmis.pharmacy.web;

import java.math.BigDecimal;

public record DrugResponse(
        Long id,
        String drugCode,
        String drugName,
        String specification,
        String unit,
        String manufacturer,
        BigDecimal salePrice,
        Integer stockWarningLine,
        String status
) {
}
