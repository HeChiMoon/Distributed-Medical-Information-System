package com.dmis.record.repository;

import com.dmis.record.model.MedicalRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    Optional<MedicalRecord> findByIdAndDeletedFalse(Long id);
    Page<MedicalRecord> findByDeletedFalse(Pageable pageable);
    Page<MedicalRecord> findByDeletedFalseAndPatientId(Long patientId, Pageable pageable);
}
