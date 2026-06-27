package com.dmis.patient.cache;

import com.dmis.patient.web.PatientResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
public class PatientCache {

    private final RedisTemplate<String, PatientResponse> redisTemplate;
    private final Duration detailTtl;

    public PatientCache(
            RedisTemplate<String, PatientResponse> redisTemplate,
            @Value("${dmis.cache.patient-detail-ttl-minutes:30}") long detailTtlMinutes
    ) {
        this.redisTemplate = redisTemplate;
        this.detailTtl = Duration.ofMinutes(detailTtlMinutes);
    }

    public Optional<PatientResponse> getDetail(Long id) {
        try {
            return Optional.ofNullable(redisTemplate.opsForValue().get(detailKey(id)));
        } catch (RuntimeException exception) {
            return Optional.empty();
        }
    }

    public void putDetail(Long id, PatientResponse response) {
        try {
            redisTemplate.opsForValue().set(detailKey(id), response, detailTtl);
        } catch (RuntimeException ignored) {
            // Cache failures should not break the clinical workflow demo path.
        }
    }

    public void evictDetail(Long id) {
        try {
            redisTemplate.delete(detailKey(id));
        } catch (RuntimeException ignored) {
            // Cache eviction is best-effort in the first milestone-2 slice.
        }
    }

    public PatientCacheDemoResponse describeDetailCache(Long id) {
        return new PatientCacheDemoResponse(
                detailKey(id),
                detailTtl.toMinutes(),
                "cache-aside: query Redis first, load MySQL on miss, then write Redis with TTL"
        );
    }

    private String detailKey(Long id) {
        return "patient:detail:" + id;
    }
}
