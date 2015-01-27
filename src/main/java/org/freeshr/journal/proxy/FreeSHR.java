package org.freeshr.journal.proxy;

import com.sun.syndication.io.FeedException;
import org.freeshr.journal.infrastructure.AtomFeed;
import org.freeshr.journal.infrastructure.WebClient;
import org.freeshr.journal.launch.ApplicationProperties;
import org.freeshr.journal.model.EncounterBundlesData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class FreeSHR {
    @Autowired
    private AtomFeed atomFeed;

    @Autowired
    private ApplicationProperties applicationProperties;

    public FreeSHR() {
    }

    public FreeSHR(AtomFeed atomFeed, ApplicationProperties applicationProperties) {
        this.atomFeed = atomFeed;
        this.applicationProperties = applicationProperties;
    }

    public EncounterBundlesData getEncountersForPatient(String patientId, Map<String, String> securityHeaders) throws
            IOException, FeedException {
        WebClient webClient = new WebClient(applicationProperties.getSHRBaseUrl(), securityHeaders);
        String response = webClient.get("/patients/" + patientId + "/encounters");
        return EncounterBundlesData.fromFeedEntries(atomFeed.parse(response));
    }
}
