package com.dmis.appointment.repository;

import com.dmis.appointment.model.AppointmentRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<AppointmentRecord, Long> {

    Optional<AppointmentRecord> findByIdAndDeletedFalse(Long id);

    Page<AppointmentRecord> findByDeletedFalse(Pageable pageable);

    Page<AppointmentRecord> findByDeletedFalseAndPatientId(Long patientId, Pageable pageable);
}
