package com.dmis.billing.api;

import com.dmis.common.api.ApiResponse;
import com.dmis.common.api.ModuleInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bills")
public class BillingController {

    @GetMapping("/modules")
    public ApiResponse<ModuleInfo> modules() {
        return ApiResponse.ok(new ModuleInfo("billing-service", "billing", List.of("bill-create", "payment", "refund")));
    }
}
