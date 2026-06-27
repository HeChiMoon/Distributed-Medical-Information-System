package com.dmis.appointment.web;

import java.time.LocalDate;

public record ScheduleResponse(
        Long id,
        Long doctorId,
        Long deptId,
        LocalDate scheduleDate,
        String timeSlot,
        int maxNumber,
        int currentNumber,
        String status
) {
}
