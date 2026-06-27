package com.dmis.patient.api;

import com.dmis.common.api.ApiResponse;
import com.dmis.common.api.PatientSummary;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PatientControllerTest {

    @Test
    void summaryReturnsDemoPatient() {
        PatientController controller = new PatientController();

        ApiResponse<PatientSummary> response = controller.summary(1L);

        assertThat(response.data().id()).isEqualTo(1L);
        assertThat(response.data().patientNo()).startsWith("P");
    }
}
