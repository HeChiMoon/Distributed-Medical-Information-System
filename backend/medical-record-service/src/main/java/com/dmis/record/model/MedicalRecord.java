package com.dmis.record.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "medical_record")
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    public String recordNo;
    public Long patientId;
    public Long appointmentId;
    public Long doctorId;
    public String chiefComplaint;
    public String presentIllness;
    public String pastHistory;
    public String physicalExam;
    public String diagnosis;
    public String advice;
    @Enumerated(EnumType.STRING)
    public RecordStatus recordStatus = RecordStatus.DRAFT;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public Long createdBy;
    public Long updatedBy;
    public Boolean deleted = false;
    public Integer version = 0;
}
