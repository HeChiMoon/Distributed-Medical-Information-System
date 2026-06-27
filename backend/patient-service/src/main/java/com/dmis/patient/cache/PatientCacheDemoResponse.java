package com.dmis.patient.cache;

public record PatientCacheDemoResponse(
        String redisKey,
        long ttlMinutes,
        String strategy
) {
}
