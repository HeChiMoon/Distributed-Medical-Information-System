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
@Table(name = "payment_record")
public class PaymentRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(nullable = false)
    public Long billId;

    @Column(nullable = false, unique = true, length = 64)
    public String paymentNo;

    @Column(nullable = false, length = 32)
    public String paymentMethod;

    @Column(nullable = false, precision = 12, scale = 2)
    public BigDecimal paymentAmount;

    public LocalDateTime paymentTime;

    @Column(nullable = false, length = 20)
    public String paymentStatus = "SUCCESS";

    public LocalDateTime createdAt;
}
