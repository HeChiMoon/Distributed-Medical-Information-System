package com.dmis.record.api;

import com.dmis.common.api.ApiResponse;
import com.dmis.common.api.ModuleInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/records")
public class MedicalRecordController {

    @GetMapping("/modules")
    public ApiResponse<ModuleInfo> modules() {
        return ApiResponse.ok(new ModuleInfo("medical-record-service", "medical-record", List.of("records", "diagnosis", "orders")));
    }
}
