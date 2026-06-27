package com.dmis.auth.service;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenServiceTest {

    @Test
    void issueTokenReturnsJwtLikeValue() {
        JwtTokenService service = new JwtTokenService("dmis-demo-secret-key-for-local-development-only-2026");

        String token = service.issueToken("admin", List.of("ADMIN"));

        assertThat(token).contains(".");
    }
}
