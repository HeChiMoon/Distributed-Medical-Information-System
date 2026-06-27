package com.dmis.appointment.api;

import com.dmis.appointment.client.PatientClient;
import com.dmis.appointment.service.AppointmentService;
import com.dmis.appointment.web.AppointmentCreateRequest;
import com.dmis.appointment.web.AppointmentResponse;
import com.dmis.appointment.web.DepartmentRequest;
import com.dmis.appointment.web.DepartmentResponse;
import com.dmis.appointment.web.DoctorRequest;
import com.dmis.appointment.web.DoctorResponse;
import com.dmis.common.api.ApiResponse;
import com.dmis.common.api.ModuleInfo;
import com.dmis.common.api.PageResponse;
import com.dmis.common.api.PatientSummary;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final PatientClient patientClient;
    private final AppointmentService appointmentService;

    public AppointmentController(PatientClient patientClient, AppointmentService appointmentService) {
        this.patientClient = patientClient;
        this.appointmentService = appointmentService;
    }

    @GetMapping("/modules")
    public ApiResponse<ModuleInfo> modules() {
        return ApiResponse.ok(new ModuleInfo("appointment-service", "appointment", List.of("schedules", "booking", "feign-patient")));
    }

    @GetMapping("/demo/remote-patient/{patientId}")
    public ApiResponse<PatientSummary> remotePatient(@PathVariable Long patientId) {
        return ApiResponse.ok(patientClient.getPatientSummary(patientId));
    }

    @PostMapping("/departments")
    public ApiResponse<DepartmentResponse> createDepartment(@Valid @RequestBody DepartmentRequest request) {
        return ApiResponse.ok(appointmentService.createDepartment(request));
    }

    @GetMapping("/departments")
    public ApiResponse<PageResponse<DepartmentResponse>> pageDepartments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<DepartmentResponse> result = appointmentService.pageDepartments(page, size);
        return ApiResponse.ok(new PageResponse<>(result.getContent(), result.getTotalElements(), result.getNumber(), result.getSize()));
    }

    @PostMapping("/doctors")
    public ApiResponse<DoctorResponse> createDoctor(@Valid @RequestBody DoctorRequest request) {
        return ApiResponse.ok(appointmentService.createDoctor(request));
    }

    @GetMapping("/doctors")
    public ApiResponse<PageResponse<DoctorResponse>> pageDoctors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<DoctorResponse> result = appointmentService.pageDoctors(page, size);
        return ApiResponse.ok(new PageResponse<>(result.getContent(), result.getTotalElements(), result.getNumber(), result.getSize()));
    }

    @PostMapping
    public ApiResponse<AppointmentResponse> book(@Valid @RequestBody AppointmentCreateRequest request) {
        return ApiResponse.ok(appointmentService.book(request));
    }

    @GetMapping
    public ApiResponse<PageResponse<AppointmentResponse>> pageAppointments(
            @RequestParam(required = false) Long patientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<AppointmentResponse> result = appointmentService.pageAppointments(patientId, page, size);
        return ApiResponse.ok(new PageResponse<>(result.getContent(), result.getTotalElements(), result.getNumber(), result.getSize()));
    }

    @GetMapping("/{id}")
    public ApiResponse<AppointmentResponse> getAppointment(@PathVariable Long id) {
        return ApiResponse.ok(appointmentService.getAppointment(id));
    }

    @PutMapping("/{id}/cancel")
    public ApiResponse<AppointmentResponse> cancel(@PathVariable Long id) {
        return ApiResponse.ok(appointmentService.cancel(id));
    }
}
