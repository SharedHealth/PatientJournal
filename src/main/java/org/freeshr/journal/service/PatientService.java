package org.freeshr.journal.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.freeshr.journal.infrastructure.WebClient;
import org.freeshr.journal.launch.ApplicationProperties;
import org.freeshr.journal.model.Patient;
import org.freeshr.journal.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

import static org.freeshr.journal.utils.HttpUtil.createSecurityHeaders;

@Component
public class PatientService {

    private ApplicationProperties properties;

    private static final Logger logger = LoggerFactory.getLogger(PatientService.class);

    @Autowired
    public PatientService(ApplicationProperties properties) {
        this.properties = properties;
    }

    public Patient getPatient(UserInfo userInfo) throws IOException {
        Map<String, String> headers = createSecurityHeaders(userInfo);
        headers.put("accept", "application/json");
        String healthId = userInfo.getPatientProfile().getId();
        String url = properties.getMciServerPatientsUrl() + healthId;
        logger.debug(String.format("Fetching Details for patient [%s] from MCI", healthId));
        String response = new WebClient().get(url, headers);
        if (StringUtils.isNotEmpty(response)) {
            return new ObjectMapper().readValue(response, Patient.class);
        }
        return null;
    }
}
