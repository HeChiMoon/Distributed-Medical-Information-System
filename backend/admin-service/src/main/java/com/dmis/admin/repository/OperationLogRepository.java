package com.dmis.admin.repository;

import com.dmis.admin.model.SysOperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OperationLogRepository extends JpaRepository<SysOperationLog, Long> {

    Page<SysOperationLog> findByModuleName(String moduleName, Pageable pageable);
}
