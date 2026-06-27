package com.dmis.appointment.repository;

import com.dmis.appointment.model.DoctorSchedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface ScheduleRepository extends JpaRepository<DoctorSchedule, Long> {

    Optional<DoctorSchedule> findByIdAndDeletedFalse(Long id);

    Page<DoctorSchedule> findByDeletedFalse(Pageable pageable);

    Page<DoctorSchedule> findByDeletedFalseAndDoctorIdAndScheduleDate(Long doctorId, LocalDate scheduleDate, Pageable pageable);
}
