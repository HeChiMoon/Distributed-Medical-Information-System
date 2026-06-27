package com.dmis.patient.api;

import com.dmis.common.api.ApiResponse;
import com.dmis.common.api.ModuleInfo;
import com.dmis.common.api.PatientSummary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {

    @GetMapping("/modules")
    public ApiResponse<ModuleInfo> modules() {
        return ApiResponse.ok(new ModuleInfo("patient-service", "patient", List.of("patient-crud", "contacts", "redis-cache")));
    }

    @GetMapping("/{id}/summary")
    public ApiResponse<PatientSummary> summary(@PathVariable Long id) {
        return ApiResponse.ok(new PatientSummary(id, "P202606270001", "Demo Patient", "13800000000"));
    }

    @GetMapping("/internal/{id}/summary")
    public PatientSummary internalSummary(@PathVariable Long id) {
        return new PatientSummary(id, "P202606270001", "Demo Patient", "13800000000");
    }
}
