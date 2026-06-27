package com.dmis.pharmacy.api;

import com.dmis.common.api.ApiResponse;
import com.dmis.common.api.ModuleInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pharmacy")
public class PharmacyController {

    @GetMapping("/modules")
    public ApiResponse<ModuleInfo> modules() {
        return ApiResponse.ok(new ModuleInfo("pharmacy-service", "pharmacy", List.of("drugs", "inventory", "dispense")));
    }
}
