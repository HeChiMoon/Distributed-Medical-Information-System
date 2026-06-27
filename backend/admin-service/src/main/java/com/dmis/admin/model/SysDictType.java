package com.dmis.admin.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "sys_dict_type")
public class SysDictType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true, length = 64)
    public String dictCode;

    @Column(nullable = false, length = 128)
    public String dictName;

    @Column(nullable = false, length = 20)
    public String status = "ENABLED";

    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public Long createdBy;
    public Long updatedBy;
    public Boolean deleted = false;
    public Integer version = 0;
}
