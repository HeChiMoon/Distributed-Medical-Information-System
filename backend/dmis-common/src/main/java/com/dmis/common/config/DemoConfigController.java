package com.dmis.common.config;

import com.dmis.common.api.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RefreshScope
@RestController
@RequestMapping("/demo")
public class DemoConfigController {

    private final String serviceName;
    private final String configMessage;

    public DemoConfigController(
            @Value("${spring.application.name:unknown-service}") String serviceName,
            @Value("${dmis.demo.config-message:local default config message}") String configMessage
    ) {
        this.serviceName = serviceName;
        this.configMessage = configMessage;
    }

    @GetMapping("/config")
    public ApiResponse<DemoConfigResponse> current() {
        return ApiResponse.ok(new DemoConfigResponse(
                serviceName,
                configMessage,
                "Modify dmis.demo.config-message in Nacos, then refresh with /actuator/refresh or Nacos auto-refresh."
        ));
    }
}
