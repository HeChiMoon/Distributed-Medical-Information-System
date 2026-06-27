package com.dmis.pharmacy.repository;

import com.dmis.pharmacy.model.DrugInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InventoryRepository extends JpaRepository<DrugInventory, Long> {

    Optional<DrugInventory> findByIdAndDeletedFalse(Long id);

    Optional<DrugInventory> findByDrugIdAndBatchNoAndDeletedFalse(Long drugId, String batchNo);

    Optional<DrugInventory> findFirstByDrugIdAndDeletedFalseOrderByExpireDateAsc(Long drugId);

    List<DrugInventory> findByDrugIdAndDeletedFalseOrderByExpireDateAsc(Long drugId);
}
