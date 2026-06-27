package com.dmis.appointment.web;

import java.time.LocalDate;

public record AppointmentResponse(
        Long id,
        String appointmentNo,
        Long patientId,
        Long doctorId,
        Long deptId,
        Long scheduleId,
        LocalDate appointmentDate,
        String timeSlot,
        String status,
        String source
) {
}
