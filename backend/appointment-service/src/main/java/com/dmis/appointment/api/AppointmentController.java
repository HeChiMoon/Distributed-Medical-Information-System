package com.dmis.appointment.api;

import com.dmis.appointment.client.PatientClient;
import com.dmis.common.api.ApiResponse;
import com.dmis.common.api.ModuleInfo;
import com.dmis.common.api.PatientSummary;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private final PatientClient patientClient;

    public AppointmentController(PatientClient patientClient) {
        this.patientClient = patientClient;
    }

    @GetMapping("/modules")
    public ApiResponse<ModuleInfo> modules() {
        return ApiResponse.ok(new ModuleInfo("appointment-service", "appointment", List.of("schedules", "booking", "feign-patient")));
    }

    @GetMapping("/demo/remote-patient/{patientId}")
    public ApiResponse<PatientSummary> remotePatient(@PathVariable Long patientId) {
        return ApiResponse.ok(patientClient.getPatientSummary(patientId));
    }
}
