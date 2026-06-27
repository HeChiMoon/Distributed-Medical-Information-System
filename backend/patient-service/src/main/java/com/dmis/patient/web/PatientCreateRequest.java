package com.dmis.patient.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PatientCreateRequest(
        @NotBlank @Size(max = 64) String name,
        @NotBlank @Size(max = 16) String gender,
        @PastOrPresent LocalDate birthday,
        @Size(max = 32) String idCard,
        @Size(max = 32) String phone,
        @Size(max = 255) String address,
        @Size(max = 16) String bloodType,
        @Size(max = 500) String allergyHistory,
        @Size(max = 500) String medicalHistory
) {
}
