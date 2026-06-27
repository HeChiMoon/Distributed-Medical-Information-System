package com.dmis.billing.repository;

import com.dmis.billing.model.PaymentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<PaymentRecord, Long> {

    List<PaymentRecord> findByBillId(Long billId);
}
