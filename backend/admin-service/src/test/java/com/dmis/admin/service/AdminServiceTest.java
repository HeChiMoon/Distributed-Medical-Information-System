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
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminServiceTest {

    private final DictTypeRepository dictTypeRepository = mock(DictTypeRepository.class);
    private final DictItemRepository dictItemRepository = mock(DictItemRepository.class);
    private final OperationLogRepository operationLogRepository = mock(OperationLogRepository.class);
    private final AdminService service = new AdminService(dictTypeRepository, dictItemRepository, operationLogRepository);

    @Test
    void createDictTypeSavesEnabledType() {
        when(dictTypeRepository.save(any(SysDictType.class))).thenAnswer(invocation -> {
            SysDictType type = invocation.getArgument(0);
            type.id = 1L;
            return type;
        });

        DictTypeResponse response = service.createDictType(new DictTypeRequest("gender", "Gender", "ENABLED"));

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.dictCode()).isEqualTo("gender");
        assertThat(response.status()).isEqualTo("ENABLED");
    }

    @Test
    void createDictItemRequiresExistingType() {
        SysDictType type = new SysDictType();
        type.id = 1L;
        type.dictCode = "gender";
        type.dictName = "Gender";
        type.status = "ENABLED";
        type.deleted = false;
        when(dictTypeRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(type));
        when(dictItemRepository.save(any(SysDictItem.class))).thenAnswer(invocation -> {
            SysDictItem item = invocation.getArgument(0);
            item.id = 2L;
            return item;
        });

        DictItemResponse response = service.createDictItem(new DictItemRequest(1L, "M", "Male", 1, "ENABLED"));

        assertThat(response.id()).isEqualTo(2L);
        assertThat(response.dictTypeId()).isEqualTo(1L);
        assertThat(response.itemCode()).isEqualTo("M");
    }

    @Test
    void createOperationLogStoresAuditTrail() {
        when(operationLogRepository.save(any(SysOperationLog.class))).thenAnswer(invocation -> {
            SysOperationLog log = invocation.getArgument(0);
            log.id = 5L;
            return log;
        });

        OperationLogResponse response = service.createOperationLog(new OperationLogRequest("patient", "CREATE", 1L, "admin", "/patients", "POST", "SUCCESS"));

        assertThat(response.id()).isEqualTo(5L);
        assertThat(response.moduleName()).isEqualTo("patient");
        assertThat(response.operationResult()).isEqualTo("SUCCESS");
    }
}
