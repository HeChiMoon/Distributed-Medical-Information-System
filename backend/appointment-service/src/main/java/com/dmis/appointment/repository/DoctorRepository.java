package com.dmis.appointment.repository;

import com.dmis.appointment.model.DoctorInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<DoctorInfo, Long> {

    Page<DoctorInfo> findByDeletedFalse(Pageable pageable);
}
