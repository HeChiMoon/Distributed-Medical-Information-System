package com.dmis.appointment.api;

import com.dmis.appointment.service.AppointmentService;
import com.dmis.appointment.web.ScheduleCreateRequest;
import com.dmis.appointment.web.ScheduleResponse;
import com.dmis.appointment.web.ScheduleUpdateRequest;
import com.dmis.common.api.ApiResponse;
import com.dmis.common.api.PageResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    private final AppointmentService appointmentService;

    public ScheduleController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping
    public ApiResponse<ScheduleResponse> createSchedule(@Valid @RequestBody ScheduleCreateRequest request) {
        return ApiResponse.ok(appointmentService.createSchedule(request));
    }

    @GetMapping
    public ApiResponse<PageResponse<ScheduleResponse>> pageSchedules(
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) LocalDate scheduleDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<ScheduleResponse> result = appointmentService.pageSchedules(doctorId, scheduleDate, page, size);
        return ApiResponse.ok(new PageResponse<>(result.getContent(), result.getTotalElements(), result.getNumber(), result.getSize()));
    }

    @GetMapping("/{id}")
    public ApiResponse<ScheduleResponse> getSchedule(@PathVariable Long id) {
        return ApiResponse.ok(appointmentService.getSchedule(id));
    }

    @PutMapping("/{id}")
    public ApiResponse<ScheduleResponse> updateSchedule(@PathVariable Long id, @Valid @RequestBody ScheduleUpdateRequest request) {
        return ApiResponse.ok(appointmentService.updateSchedule(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteSchedule(@PathVariable Long id) {
        appointmentService.deleteSchedule(id);
        return ApiResponse.ok(null);
    }
}
