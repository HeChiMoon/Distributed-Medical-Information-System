package com.dmis.appointment.repository;

import com.dmis.appointment.model.DepartmentInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<DepartmentInfo, Long> {

    Page<DepartmentInfo> findByDeletedFalse(Pageable pageable);
}
