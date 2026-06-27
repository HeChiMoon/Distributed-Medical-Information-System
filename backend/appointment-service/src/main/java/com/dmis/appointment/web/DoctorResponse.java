package com.dmis.appointment.web;

public record DoctorResponse(Long id, String doctorNo, String doctorName, Long deptId, String title, String phone, String status) {
}
