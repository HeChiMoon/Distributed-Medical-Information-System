package com.dmis.common.config;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DemoConfigControllerTest {

    @Test
    void currentReturnsRuntimeConfigMessage() {
        DemoConfigController controller = new DemoConfigController("patient-service", "changed from nacos");

        DemoConfigResponse response = controller.current().data();

        assertThat(response.serviceName()).isEqualTo("patient-service");
        assertThat(response.configMessage()).isEqualTo("changed from nacos");
        assertThat(response.refreshHint()).contains("/actuator/refresh");
    }
}
