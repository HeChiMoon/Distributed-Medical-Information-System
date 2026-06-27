package com.dmis.pharmacy.service;

import com.dmis.common.api.ApiResponse;
import com.dmis.common.error.BusinessException;
import com.dmis.pharmacy.client.MedicalOrderClient;
import com.dmis.pharmacy.model.*;
import com.dmis.pharmacy.repository.*;
import com.dmis.pharmacy.web.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PharmacyServiceTest {

    private final DrugRepository drugRepository = mock(DrugRepository.class);
    private final InventoryRepository inventoryRepository = mock(InventoryRepository.class);
    private final InventoryFlowRepository flowRepository = mock(InventoryFlowRepository.class);
    private final DispenseRepository dispenseRepository = mock(DispenseRepository.class);
    private final MedicalOrderClient orderClient = mock(MedicalOrderClient.class);
    private final PharmacyService service = new PharmacyService(drugRepository, inventoryRepository, flowRepository, dispenseRepository, orderClient);

    @Test
    void createDrugSavesEnabledDrug() {
        when(drugRepository.save(any(DrugInfo.class))).thenAnswer(invocation -> {
            DrugInfo drug = invocation.getArgument(0);
            drug.id = 1L;
            return drug;
        });

        DrugResponse response = service.createDrug(new DrugRequest("D001", "Ibuprofen", "0.2g", "box", "DMIS Pharma", new BigDecimal("12.50"), 10, "ENABLED"));

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.drugName()).isEqualTo("Ibuprofen");
    }

    @Test
    void inboundCreatesInventoryAndFlow() {
        when(drugRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(drug()));
        when(inventoryRepository.findByDrugIdAndBatchNoAndDeletedFalse(1L, "B001")).thenReturn(Optional.empty());
        when(inventoryRepository.save(any(DrugInventory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        InventoryResponse response = service.inbound(new InventoryInboundRequest(1L, "B001", 50, LocalDate.now(), LocalDate.now().plusYears(1), "A-01"));

        assertThat(response.quantity()).isEqualTo(50);
        verify(flowRepository).save(any(InventoryFlow.class));
    }

    @Test
    void dispenseRejectsInsufficientStock() {
        when(orderClient.getOrder(8L)).thenReturn(ApiResponse.ok(new MedicalOrderLookupResponse(8L, "O1", 99L, 1L, 2L, "DRUG", "Ibuprofen", "1 tablet", "BID", 3, "ACTIVE")));
        when(inventoryRepository.findFirstByDrugIdAndDeletedFalseOrderByExpireDateAsc(1L)).thenReturn(Optional.of(inventory(2)));

        assertThatThrownBy(() -> service.dispense(new DispenseRequest(1L, 99L, 8L, 1L, 5, "Pharmacist")))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("Insufficient stock");
    }

    @Test
    void dispenseDeductsStockAndCreatesRecord() {
        when(orderClient.getOrder(8L)).thenReturn(ApiResponse.ok(new MedicalOrderLookupResponse(8L, "O1", 99L, 1L, 2L, "DRUG", "Ibuprofen", "1 tablet", "BID", 3, "ACTIVE")));
        DrugInventory inventory = inventory(10);
        when(inventoryRepository.findFirstByDrugIdAndDeletedFalseOrderByExpireDateAsc(1L)).thenReturn(Optional.of(inventory));
        when(dispenseRepository.save(any(DispenseRecord.class))).thenAnswer(invocation -> {
            DispenseRecord record = invocation.getArgument(0);
            record.id = 7L;
            return record;
        });

        DispenseResponse response = service.dispense(new DispenseRequest(1L, 99L, 8L, 1L, 4, "Pharmacist"));

        assertThat(response.id()).isEqualTo(7L);
        assertThat(inventory.quantity).isEqualTo(6);
        verify(inventoryRepository).save(inventory);
        verify(flowRepository).save(any(InventoryFlow.class));
    }

    private DrugInfo drug() {
        DrugInfo drug = new DrugInfo();
        drug.id = 1L;
        drug.drugCode = "D001";
        drug.drugName = "Ibuprofen";
        drug.salePrice = new BigDecimal("12.50");
        drug.stockWarningLine = 10;
        drug.status = "ENABLED";
        drug.deleted = false;
        return drug;
    }

    private DrugInventory inventory(int quantity) {
        DrugInventory inventory = new DrugInventory();
        inventory.id = 1L;
        inventory.drugId = 1L;
        inventory.batchNo = "B001";
        inventory.quantity = quantity;
        inventory.lockedQuantity = 0;
        inventory.expireDate = LocalDate.now().plusYears(1);
        inventory.deleted = false;
        return inventory;
    }
}
