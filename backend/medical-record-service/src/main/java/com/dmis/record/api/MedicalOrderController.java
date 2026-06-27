package com.dmis.record.api;

import com.dmis.common.api.ApiResponse;
import com.dmis.common.api.PageResponse;
import com.dmis.record.service.MedicalRecordService;
import com.dmis.record.web.MedicalOrderCreateRequest;
import com.dmis.record.web.MedicalOrderResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class MedicalOrderController {

    private final MedicalRecordService medicalRecordService;

    public MedicalOrderController(MedicalRecordService medicalRecordService) {
        this.medicalRecordService = medicalRecordService;
    }

    @PostMapping
    public ApiResponse<MedicalOrderResponse> create(@Valid @RequestBody MedicalOrderCreateRequest request) {
        return ApiResponse.ok(medicalRecordService.createOrder(request));
    }

    @GetMapping
    public ApiResponse<PageResponse<MedicalOrderResponse>> page(
            @RequestParam(required = false) Long recordId,
            @RequestParam(required = false) Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<MedicalOrderResponse> result = medicalRecordService.pageOrders(recordId, patientId, page, size);
        return ApiResponse.ok(new PageResponse<>(result.getContent(), result.getTotalElements(), result.getNumber(), result.getSize()));
    }

    @GetMapping("/{id}")
    public ApiResponse<MedicalOrderResponse> detail(@PathVariable Long id) {
        return ApiResponse.ok(medicalRecordService.getOrder(id));
    }

    @PutMapping("/{id}/stop")
    public ApiResponse<MedicalOrderResponse> stop(@PathVariable Long id) {
        return ApiResponse.ok(medicalRecordService.stopOrder(id));
    }
}
