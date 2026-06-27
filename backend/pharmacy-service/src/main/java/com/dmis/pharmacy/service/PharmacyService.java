package com.dmis.pharmacy.service;

import com.dmis.common.api.ApiResponse;
import com.dmis.common.error.BusinessException;
import com.dmis.common.error.ErrorCode;
import com.dmis.pharmacy.client.MedicalOrderClient;
import com.dmis.pharmacy.model.DispenseRecord;
import com.dmis.pharmacy.model.DrugInfo;
import com.dmis.pharmacy.model.DrugInventory;
import com.dmis.pharmacy.model.InventoryFlow;
import com.dmis.pharmacy.repository.DispenseRepository;
import com.dmis.pharmacy.repository.DrugRepository;
import com.dmis.pharmacy.repository.InventoryFlowRepository;
import com.dmis.pharmacy.repository.InventoryRepository;
import com.dmis.pharmacy.web.DispenseRequest;
import com.dmis.pharmacy.web.DispenseResponse;
import com.dmis.pharmacy.web.DrugRequest;
import com.dmis.pharmacy.web.DrugResponse;
import com.dmis.pharmacy.web.InventoryInboundRequest;
import com.dmis.pharmacy.web.InventoryResponse;
import com.dmis.pharmacy.web.MedicalOrderLookupResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class PharmacyService {

    private static final DateTimeFormatter NO_DATE = DateTimeFormatter.BASIC_ISO_DATE;

    private final DrugRepository drugRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryFlowRepository flowRepository;
    private final DispenseRepository dispenseRepository;
    private final MedicalOrderClient orderClient;

    public PharmacyService(
            DrugRepository drugRepository,
            InventoryRepository inventoryRepository,
            InventoryFlowRepository flowRepository,
            DispenseRepository dispenseRepository,
            MedicalOrderClient orderClient
    ) {
        this.drugRepository = drugRepository;
        this.inventoryRepository = inventoryRepository;
        this.flowRepository = flowRepository;
        this.dispenseRepository = dispenseRepository;
        this.orderClient = orderClient;
    }

    @Transactional
    public DrugResponse createDrug(DrugRequest request) {
        DrugInfo drug = new DrugInfo();
        drug.drugCode = request.drugCode();
        drug.drugName = request.drugName();
        drug.specification = request.specification();
        drug.unit = request.unit();
        drug.manufacturer = request.manufacturer();
        drug.salePrice = request.salePrice();
        drug.stockWarningLine = request.stockWarningLine();
        drug.status = request.status() == null ? "ENABLED" : request.status().toUpperCase(Locale.ROOT);
        drug.deleted = false;
        drug.createdAt = LocalDateTime.now();
        drug.updatedAt = LocalDateTime.now();
        return toDrugResponse(drugRepository.save(drug));
    }

    @Transactional(readOnly = true)
    public Page<DrugResponse> pageDrugs(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        Page<DrugInfo> drugs = keyword == null || keyword.isBlank()
                ? drugRepository.findByDeletedFalse(pageable)
                : drugRepository.findByDeletedFalseAndDrugNameContaining(keyword, pageable);
        return drugs.map(this::toDrugResponse);
    }

    @Transactional(readOnly = true)
    public DrugResponse getDrug(Long id) {
        return toDrugResponse(loadDrug(id));
    }

    @Transactional
    public DrugResponse updateDrug(Long id, DrugRequest request) {
        DrugInfo drug = loadDrug(id);
        drug.drugCode = request.drugCode();
        drug.drugName = request.drugName();
        drug.specification = request.specification();
        drug.unit = request.unit();
        drug.manufacturer = request.manufacturer();
        drug.salePrice = request.salePrice();
        drug.stockWarningLine = request.stockWarningLine();
        drug.status = request.status() == null ? drug.status : request.status().toUpperCase(Locale.ROOT);
        drug.updatedAt = LocalDateTime.now();
        return toDrugResponse(drugRepository.save(drug));
    }

    @Transactional
    public void deleteDrug(Long id) {
        DrugInfo drug = loadDrug(id);
        drug.deleted = true;
        drug.updatedAt = LocalDateTime.now();
        drugRepository.save(drug);
    }

    @Transactional
    public InventoryResponse inbound(InventoryInboundRequest request) {
        loadDrug(request.drugId());
        DrugInventory inventory = inventoryRepository.findByDrugIdAndBatchNoAndDeletedFalse(request.drugId(), request.batchNo())
                .orElseGet(DrugInventory::new);
        boolean created = inventory.id == null;
        int before = inventory.quantity == null ? 0 : inventory.quantity;

        inventory.drugId = request.drugId();
        inventory.batchNo = request.batchNo();
        inventory.quantity = before + request.quantity();
        inventory.lockedQuantity = inventory.lockedQuantity == null ? 0 : inventory.lockedQuantity;
        inventory.productionDate = request.productionDate();
        inventory.expireDate = request.expireDate();
        inventory.warehouseLocation = request.warehouseLocation();
        inventory.deleted = false;
        if (created) {
            inventory.createdAt = LocalDateTime.now();
        }
        inventory.updatedAt = LocalDateTime.now();

        DrugInventory saved = inventoryRepository.save(inventory);
        saveFlow(request.drugId(), request.batchNo(), "INBOUND", request.quantity(), before, saved.quantity, null, "Drug inbound");
        return toInventoryResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<InventoryResponse> listInventory(Long drugId) {
        return inventoryRepository.findByDrugIdAndDeletedFalseOrderByExpireDateAsc(drugId)
                .stream()
                .map(this::toInventoryResponse)
                .toList();
    }

    @Transactional
    public DispenseResponse dispense(DispenseRequest request) {
        MedicalOrderLookupResponse order = loadOrder(request.orderId());
        if (!request.patientId().equals(order.patientId())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Order patient does not match dispense patient");
        }
        if (!"ACTIVE".equalsIgnoreCase(order.status())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Only active orders can be dispensed");
        }

        DrugInventory inventory = inventoryRepository.findFirstByDrugIdAndDeletedFalseOrderByExpireDateAsc(request.drugId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Drug inventory not found"));
        int before = inventory.quantity == null ? 0 : inventory.quantity;
        if (before < request.quantity()) {
            throw new BusinessException(ErrorCode.CONFLICT, "Insufficient stock");
        }

        inventory.quantity = before - request.quantity();
        inventory.updatedAt = LocalDateTime.now();
        inventoryRepository.save(inventory);

        DispenseRecord record = new DispenseRecord();
        record.dispenseNo = generateNo("D");
        record.patientId = request.patientId();
        record.recordId = request.recordId() == null ? order.recordId() : request.recordId();
        record.orderId = request.orderId();
        record.drugId = request.drugId();
        record.quantity = request.quantity();
        record.dispenseStatus = "DISPENSED";
        record.dispenseTime = LocalDateTime.now();
        record.pharmacistName = request.pharmacistName();
        record.deleted = false;
        record.createdAt = LocalDateTime.now();
        record.updatedAt = LocalDateTime.now();
        DispenseRecord saved = dispenseRepository.save(record);

        saveFlow(request.drugId(), inventory.batchNo, "DISPENSE", -request.quantity(), before, inventory.quantity, saved.dispenseNo, "Drug dispense");
        return toDispenseResponse(saved);
    }

    @Transactional(readOnly = true)
    public Page<DispenseResponse> pageDispense(Long patientId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        Page<DispenseRecord> records = patientId == null
                ? dispenseRepository.findByDeletedFalse(pageable)
                : dispenseRepository.findByDeletedFalseAndPatientId(patientId, pageable);
        return records.map(this::toDispenseResponse);
    }

    @Transactional(readOnly = true)
    public DispenseResponse getDispense(Long id) {
        return toDispenseResponse(dispenseRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Dispense record not found")));
    }

    private DrugInfo loadDrug(Long id) {
        return drugRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Drug not found"));
    }

    private MedicalOrderLookupResponse loadOrder(Long orderId) {
        ApiResponse<MedicalOrderLookupResponse> response = orderClient.getOrder(orderId);
        if (response == null || response.data() == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "Medical order not found");
        }
        return response.data();
    }

    private void saveFlow(Long drugId, String batchNo, String flowType, Integer changeQuantity, Integer before, Integer after, String bizNo, String remark) {
        InventoryFlow flow = new InventoryFlow();
        flow.drugId = drugId;
        flow.batchNo = batchNo;
        flow.flowType = flowType;
        flow.changeQuantity = changeQuantity;
        flow.beforeQuantity = before;
        flow.afterQuantity = after;
        flow.bizNo = bizNo;
        flow.remark = remark;
        flow.createdAt = LocalDateTime.now();
        flowRepository.save(flow);
    }

    private String generateNo(String prefix) {
        return prefix + LocalDate.now().format(NO_DATE) + UUID.randomUUID().toString().substring(0, 6).toUpperCase(Locale.ROOT);
    }

    private DrugResponse toDrugResponse(DrugInfo drug) {
        return new DrugResponse(drug.id, drug.drugCode, drug.drugName, drug.specification, drug.unit, drug.manufacturer, drug.salePrice, drug.stockWarningLine, drug.status);
    }

    private InventoryResponse toInventoryResponse(DrugInventory inventory) {
        return new InventoryResponse(inventory.id, inventory.drugId, inventory.batchNo, inventory.quantity, inventory.lockedQuantity, inventory.productionDate, inventory.expireDate, inventory.warehouseLocation);
    }

    private DispenseResponse toDispenseResponse(DispenseRecord record) {
        return new DispenseResponse(record.id, record.dispenseNo, record.patientId, record.recordId, record.orderId, record.drugId, record.quantity, record.dispenseStatus, record.dispenseTime, record.pharmacistName);
    }
}
