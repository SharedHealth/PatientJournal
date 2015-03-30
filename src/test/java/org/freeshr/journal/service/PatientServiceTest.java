package org.freeshr.journal.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.http.HttpStatus;
import org.freeshr.journal.model.Patient;
import org.freeshr.journal.model.UserInfo;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.freeshr.journal.utils.FileUtil.asString;
import static org.freeshr.journal.utils.HttpUtil.AUTH_TOKEN_KEY;
import static org.freeshr.journal.utils.HttpUtil.CLIENT_ID_KEY;
import static org.freeshr.journal.utils.HttpUtil.FROM_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientServiceTest {
    @Rule
    public WireMockRule rule= new WireMockRule(9997);

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldGetPatientDetails() throws Exception {
        PatientService patientService = new PatientService();

        givenThat(get(urlEqualTo("/patient/11112086891"))
                .withHeader("accept", equalTo("application/json"))
                .withHeader(FROM_KEY, equalTo("utsab@gmail.com"))
                .withHeader(CLIENT_ID_KEY, equalTo("12345"))
                .withHeader(AUTH_TOKEN_KEY, equalTo("00f452a2-2925-4e03-b772-971fd14982b2"))
                .willReturn(aResponse().withStatus(HttpStatus.SC_OK)
                        .withBody(asString("patient.json"))));

        Patient patient = patientService.getPatient("http://localhost:9997/patient/11112086891", getUserInfo());

        assertNotNull(patient);
        assertEquals("11112086891", patient.getHealthId());
        assertEquals("Test Patient", patient.getName());
        assertEquals("M", patient.getGender());
    }

    private UserInfo getUserInfo() throws IOException {
        String response = asString("patientUserInfo.json");
        return new ObjectMapper().readValue(response, UserInfo.class);
    }
}