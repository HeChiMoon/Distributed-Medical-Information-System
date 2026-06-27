package com.dmis.patient.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class PatientCacheDemoTest {

    @Test
    void describeDetailCacheShowsRedisKeyAndTtl() {
        PatientCache cache = new PatientCache(mock(StringRedisTemplate.class), new ObjectMapper(), 45);

        PatientCacheDemoResponse response = cache.describeDetailCache(7L);

        assertThat(response.redisKey()).isEqualTo("patient:detail:7");
        assertThat(response.ttlMinutes()).isEqualTo(45);
        assertThat(response.strategy()).contains("cache-aside");
    }
}
