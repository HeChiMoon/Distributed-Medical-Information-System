package com.dmis.billing.repository;

import com.dmis.billing.model.BillingRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillingRepository extends JpaRepository<BillingRecord, Long> {

    Optional<BillingRecord> findByIdAndDeletedFalse(Long id);

    Page<BillingRecord> findByDeletedFalse(Pageable pageable);

    Page<BillingRecord> findByDeletedFalseAndPatientId(Long patientId, Pageable pageable);
}
