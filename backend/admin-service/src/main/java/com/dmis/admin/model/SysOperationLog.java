package com.dmis.admin.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "sys_operation_log")
public class SysOperationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, length = 64)
    public String moduleName;

    @Column(nullable = false, length = 64)
    public String operationType;

    public Long operatorId;

    @Column(length = 64)
    public String operatorName;

    @Column(length = 255)
    public String requestUri;

    @Column(length = 16)
    public String requestMethod;

    @Column(length = 32)
    public String operationResult;

    public LocalDateTime operationTime;
}
