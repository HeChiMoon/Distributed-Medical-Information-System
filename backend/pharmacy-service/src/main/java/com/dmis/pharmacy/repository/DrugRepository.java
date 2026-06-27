package com.dmis.pharmacy.repository;

import com.dmis.pharmacy.model.DrugInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DrugRepository extends JpaRepository<DrugInfo, Long> {

    Optional<DrugInfo> findByIdAndDeletedFalse(Long id);

    Page<DrugInfo> findByDeletedFalse(Pageable pageable);

    Page<DrugInfo> findByDeletedFalseAndDrugNameContaining(String keyword, Pageable pageable);
}
