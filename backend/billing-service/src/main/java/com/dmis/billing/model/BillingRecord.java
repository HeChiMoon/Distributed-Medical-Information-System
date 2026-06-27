package com.dmis.billing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "billing_record")
public class BillingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false, unique = true, length = 64)
    public String billNo;

    @Column(nullable = false)
    public Long patientId;

    public Long appointmentId;
    public Long recordId;

    @Column(nullable = false, precision = 12, scale = 2)
    public BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 12, scale = 2)
    public BigDecimal paidAmount = BigDecimal.ZERO;

    @Column(nullable = false, length = 20)
    public String billStatus = "VALID";

    @Column(nullable = false, length = 20)
    public String paymentStatus = "UNPAID";

    public LocalDateTime billingTime;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public Long createdBy;
    public Long updatedBy;
    public Boolean deleted = false;
    public Integer version = 0;
}
