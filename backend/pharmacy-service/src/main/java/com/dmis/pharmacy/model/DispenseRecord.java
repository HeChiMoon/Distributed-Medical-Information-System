package com.dmis.pharmacy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "dispense_record")
public class DispenseRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true, length = 64)
    public String dispenseNo;

    @Column(nullable = false)
    public Long patientId;

    public Long recordId;
    public Long orderId;

    @Column(nullable = false)
    public Long drugId;

    @Column(nullable = false)
    public Integer quantity;

    @Column(nullable = false, length = 20)
    public String dispenseStatus = "PENDING";

    public LocalDateTime dispenseTime;

    @Column(length = 64)
    public String pharmacistName;

    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public Long createdBy;
    public Long updatedBy;
    public Boolean deleted = false;
    public Integer version = 0;
}
