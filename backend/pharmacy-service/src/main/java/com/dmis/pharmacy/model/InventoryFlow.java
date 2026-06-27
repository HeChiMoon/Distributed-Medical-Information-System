package com.dmis.pharmacy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_flow")
public class InventoryFlow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public Long drugId;

    @Column(length = 64)
    public String batchNo;

    @Column(nullable = false, length = 32)
    public String flowType;

    @Column(nullable = false)
    public Integer changeQuantity;

    @Column(nullable = false)
    public Integer beforeQuantity;

    @Column(nullable = false)
    public Integer afterQuantity;

    @Column(length = 64)
    public String bizNo;

    @Column(length = 255)
    public String remark;

    public LocalDateTime createdAt;
}
