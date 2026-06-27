package com.dmis.pharmacy.repository;

import com.dmis.pharmacy.model.DispenseRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DispenseRepository extends JpaRepository<DispenseRecord, Long> {

    Optional<DispenseRecord> findByIdAndDeletedFalse(Long id);

    Page<DispenseRecord> findByDeletedFalse(Pageable pageable);

    Page<DispenseRecord> findByDeletedFalseAndPatientId(Long patientId, Pageable pageable);
}
