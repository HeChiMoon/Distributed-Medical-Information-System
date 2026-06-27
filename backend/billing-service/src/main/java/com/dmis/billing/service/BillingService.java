package com.dmis.billing.service;

import com.dmis.billing.client.PatientClient;
import com.dmis.billing.model.BillingItem;
import com.dmis.billing.model.BillingRecord;
import com.dmis.billing.model.PaymentRecord;
import com.dmis.billing.repository.BillingItemRepository;
import com.dmis.billing.repository.BillingRepository;
import com.dmis.billing.repository.PaymentRepository;
import com.dmis.billing.web.BillingCreateRequest;
import com.dmis.billing.web.BillingItemResponse;
import com.dmis.billing.web.BillingResponse;
import com.dmis.billing.web.PaymentRequest;
import com.dmis.billing.web.PaymentResponse;
import com.dmis.common.error.BusinessException;
import com.dmis.common.error.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class BillingService {

    private static final DateTimeFormatter NO_DATE = DateTimeFormatter.BASIC_ISO_DATE;

    private final BillingRepository billingRepository;
    private final BillingItemRepository itemRepository;
    private final PaymentRepository paymentRepository;
    private final PatientClient patientClient;

    public BillingService(
            BillingRepository billingRepository,
            BillingItemRepository itemRepository,
            PaymentRepository paymentRepository,
            PatientClient patientClient
    ) {
        this.billingRepository = billingRepository;
        this.itemRepository = itemRepository;
        this.paymentRepository = paymentRepository;
        this.patientClient = patientClient;
    }

    @Transactional
    public BillingResponse createBill(BillingCreateRequest request) {
        patientClient.getPatientSummary(request.patientId());
        BigDecimal total = request.items().stream()
                .map(item -> item.unitPrice().multiply(BigDecimal.valueOf(item.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BillingRecord bill = new BillingRecord();
        bill.billNo = generateNo("B");
        bill.patientId = request.patientId();
        bill.appointmentId = request.appointmentId();
        bill.recordId = request.recordId();
        bill.totalAmount = total;
        bill.paidAmount = BigDecimal.ZERO;
        bill.billStatus = "VALID";
        bill.paymentStatus = "UNPAID";
        bill.billingTime = LocalDateTime.now();
        bill.createdAt = LocalDateTime.now();
        bill.updatedAt = LocalDateTime.now();
        bill.deleted = false;
        BillingRecord saved = billingRepository.save(bill);

        List<BillingItem> items = request.items().stream().map(item -> {
            BillingItem entity = new BillingItem();
            entity.billId = saved.id;
            entity.itemType = item.itemType();
            entity.itemName = item.itemName();
            entity.unitPrice = item.unitPrice();
            entity.quantity = item.quantity();
            entity.amount = item.unitPrice().multiply(BigDecimal.valueOf(item.quantity()));
            entity.createdAt = LocalDateTime.now();
            return entity;
        }).toList();
        itemRepository.saveAll(items);

        return toBillingResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<BillingResponse> pageBills(Long patientId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        Page<BillingRecord> bills = patientId == null
                ? billingRepository.findByDeletedFalse(pageable)
                : billingRepository.findByDeletedFalseAndPatientId(patientId, pageable);
        return bills.map(this::toBillingResponse);
    }

    @Transactional(readOnly = true)
    public BillingResponse getBill(Long id) {
        return toBillingResponse(loadBill(id));
    }

    @Transactional(readOnly = true)
    public List<BillingItemResponse> listItems(Long billId) {
        return itemRepository.findByBillId(billId).stream().map(this::toItemResponse).toList();
    }

    @Transactional
    public BillingResponse cancel(Long id) {
        BillingRecord bill = loadBill(id);
        if (bill.paidAmount.compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "Paid bill cannot be cancelled");
        }
        bill.billStatus = "CANCELLED";
        bill.updatedAt = LocalDateTime.now();
        return toBillingResponse(billingRepository.save(bill));
    }

    @Transactional
    public PaymentResponse pay(Long billId, PaymentRequest request) {
        BillingRecord bill = loadBill(billId);
        if ("CANCELLED".equalsIgnoreCase(bill.billStatus)) {
            throw new BusinessException(ErrorCode.CONFLICT, "Cannot pay cancelled bill");
        }
        BigDecimal newPaid = bill.paidAmount.add(request.paymentAmount());
        if (newPaid.compareTo(bill.totalAmount) > 0) {
            throw new BusinessException(ErrorCode.CONFLICT, "Payment amount exceeds unpaid amount");
        }

        PaymentRecord payment = new PaymentRecord();
        payment.billId = billId;
        payment.paymentNo = generateNo("P");
        payment.paymentMethod = request.paymentMethod().toUpperCase(Locale.ROOT);
        payment.paymentAmount = request.paymentAmount();
        payment.paymentTime = LocalDateTime.now();
        payment.paymentStatus = "SUCCESS";
        payment.createdAt = LocalDateTime.now();
        PaymentRecord savedPayment = paymentRepository.save(payment);

        bill.paidAmount = newPaid;
        bill.paymentStatus = newPaid.compareTo(BigDecimal.ZERO) == 0
                ? "UNPAID"
                : newPaid.compareTo(bill.totalAmount) == 0 ? "PAID" : "PARTIAL";
        bill.updatedAt = LocalDateTime.now();
        billingRepository.save(bill);
        return toPaymentResponse(savedPayment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> listPayments(Long billId) {
        return paymentRepository.findByBillId(billId).stream().map(this::toPaymentResponse).toList();
    }

    private BillingRecord loadBill(Long id) {
        return billingRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Bill not found"));
    }

    private String generateNo(String prefix) {
        return prefix + LocalDate.now().format(NO_DATE) + UUID.randomUUID().toString().substring(0, 6).toUpperCase(Locale.ROOT);
    }

    private BillingResponse toBillingResponse(BillingRecord bill) {
        return new BillingResponse(bill.id, bill.billNo, bill.patientId, bill.appointmentId, bill.recordId, bill.totalAmount, bill.paidAmount, bill.billStatus, bill.paymentStatus, bill.billingTime);
    }

    private BillingItemResponse toItemResponse(BillingItem item) {
        return new BillingItemResponse(item.id, item.billId, item.itemType, item.itemName, item.unitPrice, item.quantity, item.amount);
    }

    private PaymentResponse toPaymentResponse(PaymentRecord payment) {
        return new PaymentResponse(payment.id, payment.billId, payment.paymentNo, payment.paymentMethod, payment.paymentAmount, payment.paymentTime, payment.paymentStatus);
    }
}
