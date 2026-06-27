package com.dmis.common.config;

public record DemoConfigResponse(
        String serviceName,
        String configMessage,
        String refreshHint
) {
}
