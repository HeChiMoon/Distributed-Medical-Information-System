package com.dmis.admin.web;

public record DictTypeResponse(
        Long id,
        String dictCode,
        String dictName,
        String status
) {
}
