package com.dmis.patient.api;

import com.dmis.common.api.ApiResponse;
import com.dmis.common.api.ModuleInfo;
import com.dmis.common.api.PageResponse;
import com.dmis.common.api.PatientSummary;
import com.dmis.patient.cache.PatientCache;
import com.dmis.patient.cache.PatientCacheDemoResponse;
import com.dmis.patient.service.PatientService;
import com.dmis.patient.web.PatientCreateRequest;
import com.dmis.patient.web.PatientResponse;
import com.dmis.patient.web.PatientUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/patients")
public class PatientController {

    private final PatientService patientService;
    private final PatientCache patientCache;

    public PatientController(PatientService patientService, PatientCache patientCache) {
        this.patientService = patientService;
        this.patientCache = patientCache;
    }

    @GetMapping("/modules")
    public ApiResponse<ModuleInfo> modules() {
        return ApiResponse.ok(new ModuleInfo("patient-service", "patient", List.of("patient-crud", "contacts", "redis-cache")));
    }

    @PostMapping
    public ApiResponse<PatientResponse> create(@Valid @RequestBody PatientCreateRequest request) {
        return ApiResponse.ok(patientService.create(request));
    }

    @GetMapping
    public ApiResponse<PageResponse<PatientResponse>> page(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<PatientResponse> result = patientService.page(keyword, page, size);
        return ApiResponse.ok(new PageResponse<>(result.getContent(), result.getTotalElements(), result.getNumber(), result.getSize()));
    }

    @GetMapping("/{id}")
    public ApiResponse<PatientResponse> detail(@PathVariable Long id) {
        return ApiResponse.ok(patientService.getDetail(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<PatientResponse> update(@PathVariable Long id, @Valid @RequestBody PatientUpdateRequest request) {
        return ApiResponse.ok(patientService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        patientService.delete(id);
        return ApiResponse.ok(null);
    }

    @GetMapping("/{id}/summary")
    public ApiResponse<PatientSummary> summary(@PathVariable Long id) {
        PatientResponse patient = patientService.getSummary(id);
        return ApiResponse.ok(new PatientSummary(patient.id(), patient.patientNo(), patient.name(), patient.phone()));
    }

    @GetMapping("/{id}/cache-demo")
    public ApiResponse<PatientCacheDemoResponse> cacheDemo(@PathVariable Long id) {
        return ApiResponse.ok(patientCache.describeDetailCache(id));
    }

    @GetMapping("/internal/{id}/summary")
    public PatientSummary internalSummary(@PathVariable Long id) {
        PatientResponse patient = patientService.getSummary(id);
        return new PatientSummary(patient.id(), patient.patientNo(), patient.name(), patient.phone());
    }
}
