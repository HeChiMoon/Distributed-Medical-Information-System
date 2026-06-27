package com.dmis.gateway.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;

class JwtAuthenticationFilterTest {

    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(
            new ObjectMapper(),
            "dmis-demo-secret-key-for-local-development-only-2026"
    );

    @Test
    void protectedPathRejectsMissingBearerToken() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/patients").build());
        AtomicBoolean chained = new AtomicBoolean(false);

        filter.filter(exchange, chain(chained)).block();

        assertThat(exchange.getResponse().getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(chained).isFalse();
    }

    @Test
    void loginPathBypassesAuthentication() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.post("/api/auth/login").build());
        AtomicBoolean chained = new AtomicBoolean(false);

        filter.filter(exchange, chain(chained)).block();

        assertThat(chained).isTrue();
    }

    private GatewayFilterChain chain(AtomicBoolean chained) {
        return exchange -> {
            chained.set(true);
            return Mono.empty();
        };
    }
}
