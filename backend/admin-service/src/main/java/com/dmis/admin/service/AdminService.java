package com.dmis.admin.service;

import com.dmis.admin.model.SysDictItem;
import com.dmis.admin.model.SysDictType;
import com.dmis.admin.model.SysOperationLog;
import com.dmis.admin.repository.DictItemRepository;
import com.dmis.admin.repository.DictTypeRepository;
import com.dmis.admin.repository.OperationLogRepository;
import com.dmis.admin.web.DictItemRequest;
import com.dmis.admin.web.DictItemResponse;
import com.dmis.admin.web.DictTypeRequest;
import com.dmis.admin.web.DictTypeResponse;
import com.dmis.admin.web.OperationLogRequest;
import com.dmis.admin.web.OperationLogResponse;
import com.dmis.common.error.BusinessException;
import com.dmis.common.error.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
public class AdminService {

    private final DictTypeRepository dictTypeRepository;
    private final DictItemRepository dictItemRepository;
    private final OperationLogRepository operationLogRepository;

    public AdminService(DictTypeRepository dictTypeRepository, DictItemRepository dictItemRepository, OperationLogRepository operationLogRepository) {
        this.dictTypeRepository = dictTypeRepository;
        this.dictItemRepository = dictItemRepository;
        this.operationLogRepository = operationLogRepository;
    }

    @Transactional
    public DictTypeResponse createDictType(DictTypeRequest request) {
        SysDictType type = new SysDictType();
        type.dictCode = request.dictCode();
        type.dictName = request.dictName();
        type.status = normalizeStatus(request.status());
        type.deleted = false;
        type.createdAt = LocalDateTime.now();
        type.updatedAt = LocalDateTime.now();
        return toTypeResponse(dictTypeRepository.save(type));
    }

    @Transactional(readOnly = true)
    public Page<DictTypeResponse> pageDictTypes(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        Page<SysDictType> types = keyword == null || keyword.isBlank()
                ? dictTypeRepository.findByDeletedFalse(pageable)
                : dictTypeRepository.findByDeletedFalseAndDictNameContaining(keyword, pageable);
        return types.map(this::toTypeResponse);
    }

    @Transactional(readOnly = true)
    public DictTypeResponse getDictType(Long id) {
        return toTypeResponse(loadType(id));
    }

    @Transactional
    public DictTypeResponse updateDictType(Long id, DictTypeRequest request) {
        SysDictType type = loadType(id);
        type.dictCode = request.dictCode();
        type.dictName = request.dictName();
        type.status = normalizeStatus(request.status());
        type.updatedAt = LocalDateTime.now();
        return toTypeResponse(dictTypeRepository.save(type));
    }

    @Transactional
    public void deleteDictType(Long id) {
        SysDictType type = loadType(id);
        type.deleted = true;
        type.updatedAt = LocalDateTime.now();
        dictTypeRepository.save(type);
    }

    @Transactional
    public DictItemResponse createDictItem(DictItemRequest request) {
        loadType(request.dictTypeId());
        SysDictItem item = new SysDictItem();
        item.dictTypeId = request.dictTypeId();
        item.itemCode = request.itemCode();
        item.itemName = request.itemName();
        item.sortNo = request.sortNo() == null ? 0 : request.sortNo();
        item.status = normalizeStatus(request.status());
        item.deleted = false;
        item.createdAt = LocalDateTime.now();
        item.updatedAt = LocalDateTime.now();
        return toItemResponse(dictItemRepository.save(item));
    }

    @Transactional(readOnly = true)
    public Page<DictItemResponse> pageDictItems(Long dictTypeId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        return dictItemRepository.findByDeletedFalseAndDictTypeId(dictTypeId, pageable).map(this::toItemResponse);
    }

    @Transactional(readOnly = true)
    public DictItemResponse getDictItem(Long id) {
        return toItemResponse(loadItem(id));
    }

    @Transactional
    public DictItemResponse updateDictItem(Long id, DictItemRequest request) {
        loadType(request.dictTypeId());
        SysDictItem item = loadItem(id);
        item.dictTypeId = request.dictTypeId();
        item.itemCode = request.itemCode();
        item.itemName = request.itemName();
        item.sortNo = request.sortNo() == null ? 0 : request.sortNo();
        item.status = normalizeStatus(request.status());
        item.updatedAt = LocalDateTime.now();
        return toItemResponse(dictItemRepository.save(item));
    }

    @Transactional
    public void deleteDictItem(Long id) {
        SysDictItem item = loadItem(id);
        item.deleted = true;
        item.updatedAt = LocalDateTime.now();
        dictItemRepository.save(item);
    }

    @Transactional
    public OperationLogResponse createOperationLog(OperationLogRequest request) {
        SysOperationLog log = new SysOperationLog();
        log.moduleName = request.moduleName();
        log.operationType = request.operationType();
        log.operatorId = request.operatorId();
        log.operatorName = request.operatorName();
        log.requestUri = request.requestUri();
        log.requestMethod = request.requestMethod();
        log.operationResult = request.operationResult();
        log.operationTime = LocalDateTime.now();
        return toLogResponse(operationLogRepository.save(log));
    }

    @Transactional(readOnly = true)
    public Page<OperationLogResponse> pageOperationLogs(String moduleName, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        Page<SysOperationLog> logs = moduleName == null || moduleName.isBlank()
                ? operationLogRepository.findAll(pageable)
                : operationLogRepository.findByModuleName(moduleName, pageable);
        return logs.map(this::toLogResponse);
    }

    @Transactional(readOnly = true)
    public OperationLogResponse getOperationLog(Long id) {
        return toLogResponse(operationLogRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Operation log not found")));
    }

    private SysDictType loadType(Long id) {
        return dictTypeRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Dictionary type not found"));
    }

    private SysDictItem loadItem(Long id) {
        return dictItemRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Dictionary item not found"));
    }

    private String normalizeStatus(String status) {
        return status == null ? "ENABLED" : status.toUpperCase(Locale.ROOT);
    }

    private DictTypeResponse toTypeResponse(SysDictType type) {
        return new DictTypeResponse(type.id, type.dictCode, type.dictName, type.status);
    }

    private DictItemResponse toItemResponse(SysDictItem item) {
        return new DictItemResponse(item.id, item.dictTypeId, item.itemCode, item.itemName, item.sortNo, item.status);
    }

    private OperationLogResponse toLogResponse(SysOperationLog log) {
        return new OperationLogResponse(log.id, log.moduleName, log.operationType, log.operatorId, log.operatorName, log.requestUri, log.requestMethod, log.operationResult, log.operationTime);
    }
}
