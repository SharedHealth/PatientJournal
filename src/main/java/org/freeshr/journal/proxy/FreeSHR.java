package org.freeshr.journal.proxy;

import org.freeshr.journal.infrastructure.AtomFeed;
import org.freeshr.journal.model.EncounterBundles;
import org.hl7.fhir.instance.model.Encounter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class FreeSHR {
    @Autowired
    private EncounterBundles encounterBundles;
    @Autowired
    private AtomFeed atomFeed;

    public FreeSHR(EncounterBundles encounterBundles, AtomFeed atomFeed) {
        this.encounterBundles = encounterBundles;
        this.atomFeed = atomFeed;
    }

    public List<Encounter> getEncountersForPatient(String patientId) {
        return null;
    }
}
