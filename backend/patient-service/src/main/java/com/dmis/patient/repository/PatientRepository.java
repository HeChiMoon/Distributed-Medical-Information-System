package com.dmis.patient.repository;

import com.dmis.patient.model.PatientInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<PatientInfo, Long> {

    Optional<PatientInfo> findByIdAndDeletedFalse(Long id);

    Page<PatientInfo> findByDeletedFalse(Pageable pageable);

    Page<PatientInfo> findByDeletedFalseAndNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByIdCardAndDeletedFalse(String idCard);
}
