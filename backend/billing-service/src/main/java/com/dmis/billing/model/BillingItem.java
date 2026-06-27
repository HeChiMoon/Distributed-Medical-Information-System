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
@Table(name = "billing_item")
public class BillingItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public Long billId;

    @Column(nullable = false, length = 32)
    public String itemType;

    @Column(nullable = false, length = 128)
    public String itemName;

    @Column(nullable = false, precision = 12, scale = 2)
    public BigDecimal unitPrice;

    @Column(nullable = false)
    public Integer quantity;

    @Column(nullable = false, precision = 12, scale = 2)
    public BigDecimal amount;

    public LocalDateTime createdAt;
}
