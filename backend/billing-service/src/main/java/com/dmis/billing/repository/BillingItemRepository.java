package com.dmis.billing.repository;

import com.dmis.billing.model.BillingItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillingItemRepository extends JpaRepository<BillingItem, Long> {

    List<BillingItem> findByBillId(Long billId);
}
