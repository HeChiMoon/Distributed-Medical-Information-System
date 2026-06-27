package com.dmis.admin.api;

import com.dmis.common.api.ApiResponse;
import com.dmis.common.api.ModuleInfo;
import com.dmis.common.api.PageResponse;
import com.dmis.admin.service.AdminService;
import com.dmis.admin.web.DictItemRequest;
import com.dmis.admin.web.DictItemResponse;
import com.dmis.admin.web.DictTypeRequest;
import com.dmis.admin.web.DictTypeResponse;
import com.dmis.admin.web.OperationLogRequest;
import com.dmis.admin.web.OperationLogResponse;
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
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/modules")
    public ApiResponse<ModuleInfo> modules() {
        return ApiResponse.ok(new ModuleInfo("admin-service", "admin", List.of("dict", "config", "operation-log")));
    }

    @PostMapping("/dict-types")
    public ApiResponse<DictTypeResponse> createDictType(@Valid @RequestBody DictTypeRequest request) {
        return ApiResponse.ok(adminService.createDictType(request));
    }

    @GetMapping("/dict-types")
    public ApiResponse<PageResponse<DictTypeResponse>> pageDictTypes(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<DictTypeResponse> result = adminService.pageDictTypes(keyword, page, size);
        return ApiResponse.ok(new PageResponse<>(result.getContent(), result.getTotalElements(), result.getNumber(), result.getSize()));
    }

    @GetMapping("/dict-types/{id}")
    public ApiResponse<DictTypeResponse> getDictType(@PathVariable Long id) {
        return ApiResponse.ok(adminService.getDictType(id));
    }

    @PutMapping("/dict-types/{id}")
    public ApiResponse<DictTypeResponse> updateDictType(@PathVariable Long id, @Valid @RequestBody DictTypeRequest request) {
        return ApiResponse.ok(adminService.updateDictType(id, request));
    }

    @DeleteMapping("/dict-types/{id}")
    public ApiResponse<Void> deleteDictType(@PathVariable Long id) {
        adminService.deleteDictType(id);
        return ApiResponse.ok(null);
    }

    @PostMapping("/dict-items")
    public ApiResponse<DictItemResponse> createDictItem(@Valid @RequestBody DictItemRequest request) {
        return ApiResponse.ok(adminService.createDictItem(request));
    }

    @GetMapping("/dict-items")
    public ApiResponse<PageResponse<DictItemResponse>> pageDictItems(
            @RequestParam Long dictTypeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<DictItemResponse> result = adminService.pageDictItems(dictTypeId, page, size);
        return ApiResponse.ok(new PageResponse<>(result.getContent(), result.getTotalElements(), result.getNumber(), result.getSize()));
    }

    @GetMapping("/dict-items/{id}")
    public ApiResponse<DictItemResponse> getDictItem(@PathVariable Long id) {
        return ApiResponse.ok(adminService.getDictItem(id));
    }

    @PutMapping("/dict-items/{id}")
    public ApiResponse<DictItemResponse> updateDictItem(@PathVariable Long id, @Valid @RequestBody DictItemRequest request) {
        return ApiResponse.ok(adminService.updateDictItem(id, request));
    }

    @DeleteMapping("/dict-items/{id}")
    public ApiResponse<Void> deleteDictItem(@PathVariable Long id) {
        adminService.deleteDictItem(id);
        return ApiResponse.ok(null);
    }

    @PostMapping("/operation-logs")
    public ApiResponse<OperationLogResponse> createOperationLog(@Valid @RequestBody OperationLogRequest request) {
        return ApiResponse.ok(adminService.createOperationLog(request));
    }

    @GetMapping("/operation-logs")
    public ApiResponse<PageResponse<OperationLogResponse>> pageOperationLogs(
            @RequestParam(required = false) String moduleName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<OperationLogResponse> result = adminService.pageOperationLogs(moduleName, page, size);
        return ApiResponse.ok(new PageResponse<>(result.getContent(), result.getTotalElements(), result.getNumber(), result.getSize()));
    }

    @GetMapping("/operation-logs/{id}")
    public ApiResponse<OperationLogResponse> getOperationLog(@PathVariable Long id) {
        return ApiResponse.ok(adminService.getOperationLog(id));
    }
}
