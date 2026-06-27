package com.dmis.billing.api;

import com.dmis.common.api.ApiResponse;
import com.dmis.common.api.ModuleInfo;
import com.dmis.common.api.PageResponse;
import com.dmis.billing.service.BillingService;
import com.dmis.billing.web.BillingCreateRequest;
import com.dmis.billing.web.BillingItemResponse;
import com.dmis.billing.web.BillingResponse;
import com.dmis.billing.web.PaymentRequest;
import com.dmis.billing.web.PaymentResponse;
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

import java.util.List;

@RestController
@RequestMapping("/bills")
public class BillingController {

    private final BillingService billingService;

    public BillingController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping("/modules")
    public ApiResponse<ModuleInfo> modules() {
        return ApiResponse.ok(new ModuleInfo("billing-service", "billing", List.of("bill-create", "payment", "refund")));
    }

    @PostMapping
    public ApiResponse<BillingResponse> create(@Valid @RequestBody BillingCreateRequest request) {
        return ApiResponse.ok(billingService.createBill(request));
    }

    @GetMapping
    public ApiResponse<PageResponse<BillingResponse>> page(
            @RequestParam(required = false) Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<BillingResponse> result = billingService.pageBills(patientId, page, size);
        return ApiResponse.ok(new PageResponse<>(result.getContent(), result.getTotalElements(), result.getNumber(), result.getSize()));
    }

    @GetMapping("/{id}")
    public ApiResponse<BillingResponse> detail(@PathVariable Long id) {
        return ApiResponse.ok(billingService.getBill(id));
    }

    @GetMapping("/{id}/items")
    public ApiResponse<List<BillingItemResponse>> items(@PathVariable Long id) {
        return ApiResponse.ok(billingService.listItems(id));
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<BillingResponse> cancel(@PathVariable Long id) {
        return ApiResponse.ok(billingService.cancel(id));
    }

    @PostMapping("/{id}/payments")
    public ApiResponse<PaymentResponse> pay(@PathVariable Long id, @Valid @RequestBody PaymentRequest request) {
        return ApiResponse.ok(billingService.pay(id, request));
    }

    @GetMapping("/{id}/payments")
    public ApiResponse<List<PaymentResponse>> payments(@PathVariable Long id) {
        return ApiResponse.ok(billingService.listPayments(id));
    }
}
