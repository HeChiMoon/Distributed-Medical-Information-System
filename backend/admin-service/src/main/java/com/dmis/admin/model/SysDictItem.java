package com.dmis.admin.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "sys_dict_item")
public class SysDictItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public Long dictTypeId;

    @Column(nullable = false, length = 64)
    public String itemCode;

    @Column(nullable = false, length = 128)
    public String itemName;

    @Column(nullable = false)
    public Integer sortNo = 0;

    @Column(nullable = false, length = 20)
    public String status = "ENABLED";

    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public Long createdBy;
    public Long updatedBy;
    public Boolean deleted = false;
    public Integer version = 0;
}
