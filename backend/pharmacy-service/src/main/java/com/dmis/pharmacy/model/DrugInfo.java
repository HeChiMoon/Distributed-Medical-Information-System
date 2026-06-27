package com.dmis.pharmacy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "drug_info")
public class DrugInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true, length = 64)
    public String drugCode;

    @Column(nullable = false, length = 128)
    public String drugName;

    @Column(length = 128)
    public String specification;

    @Column(length = 32)
    public String unit;

    @Column(length = 128)
    public String manufacturer;

    @Column(nullable = false, precision = 12, scale = 2)
    public BigDecimal salePrice = BigDecimal.ZERO;

    @Column(nullable = false)
    public Integer stockWarningLine = 0;

    @Column(nullable = false, length = 20)
    public String status = "ENABLED";

    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public Long createdBy;
    public Long updatedBy;
    public Boolean deleted = false;
    public Integer version = 0;
}
