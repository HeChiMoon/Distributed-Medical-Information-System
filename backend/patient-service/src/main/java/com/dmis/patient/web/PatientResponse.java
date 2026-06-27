package com.dmis.patient.web;

import java.io.Serializable;
import java.time.LocalDate;

public record PatientResponse(
        Long id,
        String patientNo,
        String name,
        String gender,
        LocalDate birthday,
        String idCard,
        String phone,
        String address,
        String bloodType,
        String allergyHistory,
        String medicalHistory,
        String status
) implements Serializable {
}
