package com.dmis.record.repository;

import com.dmis.record.model.MedicalOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicalOrderRepository extends JpaRepository<MedicalOrder, Long> {
    Optional<MedicalOrder> findByIdAndDeletedFalse(Long id);
    Page<MedicalOrder> findByDeletedFalseAndRecordId(Long recordId, Pageable pageable);
    Page<MedicalOrder> findByDeletedFalseAndPatientId(Long patientId, Pageable pageable);
}
