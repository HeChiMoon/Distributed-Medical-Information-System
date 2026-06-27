package com.dmis.record.api;

import com.dmis.common.api.ApiResponse;
import com.dmis.common.api.ModuleInfo;
import com.dmis.common.api.PageResponse;
import com.dmis.record.service.MedicalRecordService;
import com.dmis.record.web.MedicalRecordCreateRequest;
import com.dmis.record.web.MedicalRecordResponse;
import com.dmis.record.web.MedicalRecordUpdateRequest;
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
@RequestMapping("/records")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    public MedicalRecordController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @GetMapping("/modules")
    public ApiResponse<ModuleInfo> modules() {
        return ApiResponse.ok(new ModuleInfo("medical-record-service", "medical-record", List.of("records", "diagnosis", "orders")));
    }

    @PostMapping
    public ApiResponse<MedicalRecordResponse> create(@Valid @RequestBody MedicalRecordCreateRequest request) {
        return ApiResponse.ok(medicalRecordService.createRecord(request));
    }

    @GetMapping
    public ApiResponse<PageResponse<MedicalRecordResponse>> page(
            @RequestParam(required = false) Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<MedicalRecordResponse> result = medicalRecordService.pageRecords(patientId, page, size);
        return ApiResponse.ok(new PageResponse<>(result.getContent(), result.getTotalElements(), result.getNumber(), result.getSize()));
    }

    @GetMapping("/{id}")
    public ApiResponse<MedicalRecordResponse> detail(@PathVariable Long id) {
        return ApiResponse.ok(medicalRecordService.getRecord(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<MedicalRecordResponse> update(@PathVariable Long id, @Valid @RequestBody MedicalRecordUpdateRequest request) {
        return ApiResponse.ok(medicalRecordService.updateRecord(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        medicalRecordService.deleteRecord(id);
        return ApiResponse.ok(null);
    }
}
