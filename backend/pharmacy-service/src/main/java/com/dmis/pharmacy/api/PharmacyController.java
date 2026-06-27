package com.dmis.pharmacy.api;

import com.dmis.common.api.ApiResponse;
import com.dmis.common.api.ModuleInfo;
import com.dmis.common.api.PageResponse;
import com.dmis.pharmacy.service.PharmacyService;
import com.dmis.pharmacy.web.DispenseRequest;
import com.dmis.pharmacy.web.DispenseResponse;
import com.dmis.pharmacy.web.DrugRequest;
import com.dmis.pharmacy.web.DrugResponse;
import com.dmis.pharmacy.web.InventoryInboundRequest;
import com.dmis.pharmacy.web.InventoryResponse;
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
@RequestMapping("/pharmacy")
public class PharmacyController {

    private final PharmacyService pharmacyService;

    public PharmacyController(PharmacyService pharmacyService) {
        this.pharmacyService = pharmacyService;
    }

    @GetMapping("/modules")
    public ApiResponse<ModuleInfo> modules() {
        return ApiResponse.ok(new ModuleInfo("pharmacy-service", "pharmacy", List.of("drugs", "inventory", "dispense")));
    }

    @PostMapping("/drugs")
    public ApiResponse<DrugResponse> createDrug(@Valid @RequestBody DrugRequest request) {
        return ApiResponse.ok(pharmacyService.createDrug(request));
    }

    @GetMapping("/drugs")
    public ApiResponse<PageResponse<DrugResponse>> pageDrugs(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<DrugResponse> result = pharmacyService.pageDrugs(keyword, page, size);
        return ApiResponse.ok(new PageResponse<>(result.getContent(), result.getTotalElements(), result.getNumber(), result.getSize()));
    }

    @GetMapping("/drugs/{id}")
    public ApiResponse<DrugResponse> getDrug(@PathVariable Long id) {
        return ApiResponse.ok(pharmacyService.getDrug(id));
    }

    @PutMapping("/drugs/{id}")
    public ApiResponse<DrugResponse> updateDrug(@PathVariable Long id, @Valid @RequestBody DrugRequest request) {
        return ApiResponse.ok(pharmacyService.updateDrug(id, request));
    }

    @DeleteMapping("/drugs/{id}")
    public ApiResponse<Void> deleteDrug(@PathVariable Long id) {
        pharmacyService.deleteDrug(id);
        return ApiResponse.ok(null);
    }

    @PostMapping("/inventory/inbound")
    public ApiResponse<InventoryResponse> inbound(@Valid @RequestBody InventoryInboundRequest request) {
        return ApiResponse.ok(pharmacyService.inbound(request));
    }

    @GetMapping("/inventory")
    public ApiResponse<List<InventoryResponse>> inventory(@RequestParam Long drugId) {
        return ApiResponse.ok(pharmacyService.listInventory(drugId));
    }

    @PostMapping("/dispenses")
    public ApiResponse<DispenseResponse> dispense(@Valid @RequestBody DispenseRequest request) {
        return ApiResponse.ok(pharmacyService.dispense(request));
    }

    @GetMapping("/dispenses")
    public ApiResponse<PageResponse<DispenseResponse>> pageDispenses(
            @RequestParam(required = false) Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<DispenseResponse> result = pharmacyService.pageDispense(patientId, page, size);
        return ApiResponse.ok(new PageResponse<>(result.getContent(), result.getTotalElements(), result.getNumber(), result.getSize()));
    }

    @GetMapping("/dispenses/{id}")
    public ApiResponse<DispenseResponse> getDispense(@PathVariable Long id) {
        return ApiResponse.ok(pharmacyService.getDispense(id));
    }
}
