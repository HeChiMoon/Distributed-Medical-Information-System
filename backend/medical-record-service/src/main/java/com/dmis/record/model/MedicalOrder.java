package com.dmis.record.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "medical_order")
public class MedicalOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String orderNo;
    public Long recordId;
    public Long patientId;
    public Long doctorId;
    public String orderType;
    public String orderContent;
    public String dosage;
    public String frequency;
    public Integer durationDays;
    @Enumerated(EnumType.STRING)
    public OrderStatus status = OrderStatus.ACTIVE;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public Long createdBy;
    public Long updatedBy;
    public Boolean deleted = false;
    public Integer version = 0;
}
