package org.freeshr.journal.service;


import ca.uhn.fhir.model.dstu2.composite.HumanNameDt;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.freeshr.journal.infrastructure.FhirBundleUtil;
import org.freeshr.journal.infrastructure.WebClient;
import org.freeshr.journal.launch.ApplicationProperties;
import org.freeshr.journal.model.Patient;
import org.freeshr.journal.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

import static org.freeshr.journal.utils.HttpUtil.createSecurityHeaders;

@Component
public class PatientService {

    private ApplicationProperties properties;

    private Logger logger = Logger.getLogger(PatientService.class);

    @Autowired
    public PatientService(ApplicationProperties properties) {
        this.properties = properties;
    }

    public Patient getPatient(UserInfo userInfo) throws IOException {
        Map<String, String> headers = createSecurityHeaders(userInfo);
        String healthId = userInfo.getPatientProfile().getId();
        String url = properties.getMciServerPatientsUrl() + healthId;
        logger.debug(String.format("Fetching Details for patient [%s] from MCI", healthId));
        String response = new WebClient().get(url, headers);
        if (StringUtils.isEmpty(response)) return null;
        ca.uhn.fhir.model.dstu2.resource.Patient fhirPatient = (ca.uhn.fhir.model.dstu2.resource.Patient) FhirBundleUtil.parseResource(response);

        Patient patient = new Patient();
        patient.setHealthId(healthId);
        patient.setGender(fhirPatient.getGender());
        HumanNameDt name = fhirPatient.getNameFirstRep();
        patient.setGivenName(name.getGivenFirstRep().getValue());
        patient.setSurName(name.getFamilyFirstRep().getValue());
        return patient;
    }
}
