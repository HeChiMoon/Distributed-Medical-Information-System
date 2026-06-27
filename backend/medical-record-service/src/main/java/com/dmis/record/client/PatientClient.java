package com.dmis.record.client;

import com.dmis.common.api.PatientSummary;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patient-service")
public interface PatientClient {

    @GetMapping("/patients/internal/{id}/summary")
    PatientSummary getPatientSummary(@PathVariable("id") Long id);
}
