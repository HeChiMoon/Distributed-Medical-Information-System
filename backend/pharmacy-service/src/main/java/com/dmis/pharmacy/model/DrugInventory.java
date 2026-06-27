package com.dmis.pharmacy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "drug_inventory")
public class DrugInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public Long drugId;

    @Column(nullable = false, length = 64)
    public String batchNo;

    @Column(nullable = false)
    public Integer quantity = 0;

    @Column(nullable = false)
    public Integer lockedQuantity = 0;

    public LocalDate productionDate;
    public LocalDate expireDate;

    @Column(length = 128)
    public String warehouseLocation;

    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public Long createdBy;
    public Long updatedBy;
    public Boolean deleted = false;
    public Integer version = 0;
}
