package com.dmis.record.client;

import com.dmis.common.api.ApiResponse;
import com.dmis.record.web.AppointmentLookupResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "appointment-service")
public interface AppointmentClient {

    @GetMapping("/appointments/{id}")
    ApiResponse<AppointmentLookupResponse> getAppointment(@PathVariable("id") Long id);
}
