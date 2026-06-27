package com.dmis.admin.web;

public record DictItemResponse(
        Long id,
        Long dictTypeId,
        String itemCode,
        String itemName,
        Integer sortNo,
        String status
) {
}
