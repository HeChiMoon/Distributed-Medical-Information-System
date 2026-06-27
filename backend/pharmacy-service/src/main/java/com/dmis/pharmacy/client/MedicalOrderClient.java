package com.dmis.pharmacy.client;

import com.dmis.common.api.ApiResponse;
import com.dmis.pharmacy.web.MedicalOrderLookupResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "medical-record-service")
public interface MedicalOrderClient {

    @GetMapping("/orders/{id}")
    ApiResponse<MedicalOrderLookupResponse> getOrder(@PathVariable("id") Long id);
}
