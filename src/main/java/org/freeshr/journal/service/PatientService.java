package org.freeshr.journal.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.freeshr.journal.infrastructure.WebClient;
import org.freeshr.journal.model.Patient;
import org.freeshr.journal.model.UserInfo;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

import static org.freeshr.journal.utils.HttpUtil.createSecurityHeaders;

@Component
public class PatientService {

    public Patient getPatient(String patientReference, UserInfo userInfo) throws IOException {
        Map<String, String> headers = createSecurityHeaders(userInfo);
        headers.put("accept", "application/json");
        String response = new WebClient().get(patientReference, headers);
        if (StringUtils.isNotEmpty(response)) {
            return new ObjectMapper().readValue(response, Patient.class);
        }
        return null;
    }
}
