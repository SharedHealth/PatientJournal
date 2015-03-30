package org.freeshr.journal.service;

import com.sun.syndication.io.FeedException;
import org.freeshr.journal.model.EncounterBundlesData;
import org.freeshr.journal.proxy.FreeSHR;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class EncounterService {
    @Autowired
    private FreeSHR freeSHR;

    public EncounterService() {
    }

    public EncounterService(FreeSHR freeSHR) {
        this.freeSHR = freeSHR;
    }

    public EncounterBundlesData getEncountersForPatient(String patientId, Map<String, String> securityHeaders)
            throws IOException, FeedException {
        return freeSHR.getEncountersForPatient(patientId, securityHeaders);
    }
}
