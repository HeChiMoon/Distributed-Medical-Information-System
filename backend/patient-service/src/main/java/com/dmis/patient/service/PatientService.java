package com.dmis.patient.service;

import com.dmis.common.error.BusinessException;
import com.dmis.common.error.ErrorCode;
import com.dmis.patient.cache.PatientCache;
import com.dmis.patient.model.PatientInfo;
import com.dmis.patient.model.PatientStatus;
import com.dmis.patient.repository.PatientRepository;
import com.dmis.patient.web.PatientCreateRequest;
import com.dmis.patient.web.PatientResponse;
import com.dmis.patient.web.PatientUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

@Service
public class PatientService {

    private static final DateTimeFormatter PATIENT_NO_DATE = DateTimeFormatter.BASIC_ISO_DATE;

    private final PatientRepository repository;
    private final PatientCache cache;

    public PatientService(PatientRepository repository, PatientCache cache) {
        this.repository = repository;
        this.cache = cache;
    }

    @Transactional
    public PatientResponse create(PatientCreateRequest request) {
        if (StringUtils.hasText(request.idCard()) && repository.existsByIdCardAndDeletedFalse(request.idCard())) {
            throw new BusinessException(ErrorCode.CONFLICT, "Patient id card already exists");
        }

        PatientInfo patient = new PatientInfo();
        patient.setPatientNo(generatePatientNo());
        patient.setName(request.name());
        patient.setGender(request.gender());
        patient.setBirthday(request.birthday());
        patient.setIdCard(request.idCard());
        patient.setPhone(request.phone());
        patient.setAddress(request.address());
        patient.setBloodType(request.bloodType());
        patient.setAllergyHistory(request.allergyHistory());
        patient.setMedicalHistory(request.medicalHistory());
        patient.setStatus(PatientStatus.ACTIVE);
        patient.setDeleted(false);
        patient.setCreatedAt(LocalDateTime.now());
        patient.setUpdatedAt(LocalDateTime.now());

        return toResponse(repository.save(patient));
    }

    @Transactional(readOnly = true)
    public Page<PatientResponse> page(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        Page<PatientInfo> patients = StringUtils.hasText(keyword)
                ? repository.findByDeletedFalseAndNameContainingIgnoreCase(keyword, pageable)
                : repository.findByDeletedFalse(pageable);
        return patients.map(this::toResponse);
    }

    @Transactional(readOnly = true)
    public PatientResponse getDetail(Long id) {
        return cache.getDetail(id).orElseGet(() -> {
            PatientResponse response = toResponse(loadPatient(id));
            cache.putDetail(id, response);
            return response;
        });
    }

    @Transactional
    public PatientResponse update(Long id, PatientUpdateRequest request) {
        PatientInfo patient = loadPatient(id);
        patient.setName(request.name());
        patient.setGender(request.gender());
        patient.setBirthday(request.birthday());
        patient.setPhone(request.phone());
        patient.setAddress(request.address());
        patient.setBloodType(request.bloodType());
        patient.setAllergyHistory(request.allergyHistory());
        patient.setMedicalHistory(request.medicalHistory());
        patient.setStatus(parseStatus(request.status()));
        patient.setUpdatedAt(LocalDateTime.now());

        PatientResponse response = toResponse(repository.save(patient));
        cache.evictDetail(id);
        return response;
    }

    @Transactional
    public void delete(Long id) {
        PatientInfo patient = loadPatient(id);
        patient.setDeleted(true);
        patient.setUpdatedAt(LocalDateTime.now());
        repository.save(patient);
        cache.evictDetail(id);
    }

    @Transactional(readOnly = true)
    public PatientResponse getSummary(Long id) {
        return toResponse(loadPatient(id));
    }

    private PatientInfo loadPatient(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "Patient not found"));
    }

    private PatientStatus parseStatus(String status) {
        try {
            return PatientStatus.valueOf(status.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "Unsupported patient status");
        }
    }

    private String generatePatientNo() {
        return "P" + LocalDate.now().format(PATIENT_NO_DATE) + UUID.randomUUID().toString().substring(0, 6).toUpperCase(Locale.ROOT);
    }

    private PatientResponse toResponse(PatientInfo patient) {
        return new PatientResponse(
                patient.getId(),
                patient.getPatientNo(),
                patient.getName(),
                patient.getGender(),
                patient.getBirthday(),
                patient.getIdCard(),
                patient.getPhone(),
                patient.getAddress(),
                patient.getBloodType(),
                patient.getAllergyHistory(),
                patient.getMedicalHistory(),
                patient.getStatus().name()
        );
    }
}
