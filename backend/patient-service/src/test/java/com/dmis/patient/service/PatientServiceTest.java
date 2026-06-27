package com.dmis.patient.service;

import com.dmis.patient.cache.PatientCache;
import com.dmis.patient.model.PatientInfo;
import com.dmis.patient.model.PatientStatus;
import com.dmis.patient.repository.PatientRepository;
import com.dmis.patient.web.PatientCreateRequest;
import com.dmis.patient.web.PatientResponse;
import com.dmis.patient.web.PatientUpdateRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PatientServiceTest {

    private final PatientRepository repository = mock(PatientRepository.class);
    private final PatientCache cache = mock(PatientCache.class);
    private final PatientService service = new PatientService(repository, cache);

    @Test
    void createPatientGeneratesPatientNoAndSavesActiveRecord() {
        when(repository.existsByIdCardAndDeletedFalse("110101199001010011")).thenReturn(false);
        when(repository.save(any(PatientInfo.class))).thenAnswer(invocation -> {
            PatientInfo patient = invocation.getArgument(0);
            patient.setId(1L);
            return patient;
        });

        PatientResponse response = service.create(new PatientCreateRequest(
                "Alice Chen",
                "FEMALE",
                LocalDate.of(1990, 1, 1),
                "110101199001010011",
                "13800000001",
                "Beijing",
                "A",
                "Penicillin",
                "Hypertension"
        ));

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.patientNo()).startsWith("P");
        assertThat(response.status()).isEqualTo(PatientStatus.ACTIVE.name());

        ArgumentCaptor<PatientInfo> captor = ArgumentCaptor.forClass(PatientInfo.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getName()).isEqualTo("Alice Chen");
    }

    @Test
    void getDetailReturnsCachedPatientWithoutLoadingDatabase() {
        PatientResponse cached = new PatientResponse(
                1L,
                "P202606270001",
                "Cached Patient",
                "MALE",
                LocalDate.of(1988, 3, 2),
                "110101198803020011",
                "13800000002",
                "Shanghai",
                "O",
                "",
                "",
                PatientStatus.ACTIVE.name()
        );
        when(cache.getDetail(1L)).thenReturn(Optional.of(cached));

        PatientResponse response = service.getDetail(1L);

        assertThat(response.name()).isEqualTo("Cached Patient");
        verify(repository, never()).findByIdAndDeletedFalse(1L);
    }

    @Test
    void updatePatientSavesChangesAndEvictsCache() {
        PatientInfo patient = patient(1L, "P202606270001", "Old Name");
        when(repository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(patient));
        when(repository.save(any(PatientInfo.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PatientResponse response = service.update(1L, new PatientUpdateRequest(
                "New Name",
                "MALE",
                LocalDate.of(1988, 3, 2),
                "13800000003",
                "Guangzhou",
                "B",
                "None",
                "None",
                PatientStatus.INACTIVE.name()
        ));

        assertThat(response.name()).isEqualTo("New Name");
        assertThat(response.status()).isEqualTo(PatientStatus.INACTIVE.name());
        verify(cache).evictDetail(1L);
    }

    @Test
    void deletePatientMarksRecordDeletedAndEvictsCache() {
        PatientInfo patient = patient(1L, "P202606270001", "Delete Me");
        when(repository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(patient));

        service.delete(1L);

        assertThat(patient.getDeleted()).isTrue();
        verify(repository).save(patient);
        verify(cache).evictDetail(1L);
    }

    @Test
    void pagePatientsReturnsRepositoryPage() {
        when(repository.findByDeletedFalse(PageRequest.of(0, 10)))
                .thenReturn(new PageImpl<>(List.of(patient(1L, "P202606270001", "Alice"))));

        Page<PatientResponse> page = service.page(null, 0, 10);

        assertThat(page.getTotalElements()).isEqualTo(1);
        assertThat(page.getContent().get(0).name()).isEqualTo("Alice");
    }

    private PatientInfo patient(Long id, String patientNo, String name) {
        PatientInfo patient = new PatientInfo();
        patient.setId(id);
        patient.setPatientNo(patientNo);
        patient.setName(name);
        patient.setGender("MALE");
        patient.setBirthday(LocalDate.of(1988, 3, 2));
        patient.setIdCard("110101198803020011");
        patient.setPhone("13800000002");
        patient.setAddress("Shanghai");
        patient.setBloodType("O");
        patient.setAllergyHistory("");
        patient.setMedicalHistory("");
        patient.setStatus(PatientStatus.ACTIVE);
        patient.setDeleted(false);
        return patient;
    }
}
