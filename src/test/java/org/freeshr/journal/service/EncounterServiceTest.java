package org.freeshr.journal.service;

import org.freeshr.journal.model.EncounterBundle;
import org.freeshr.journal.model.EncounterBundleData;
import org.freeshr.journal.model.EncounterBundlesData;
import org.freeshr.journal.proxy.FreeSHR;
import org.hl7.fhir.dstu3.model.Encounter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EncounterServiceTest {
    @Mock
    private FreeSHR freeSHR;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldGetEncounterFromSHR() throws Exception {
        String patientId = "5952907233237925889";
        EncounterBundlesData value = new EncounterBundlesData();
        EncounterBundle encounterBundle = new EncounterBundle();
        encounterBundle.addResource(new Encounter());
        value.addEncounterBundleModel(new EncounterBundleData(encounterBundle));
        when(freeSHR.getEncountersForPatient(patientId, null)).thenReturn(value);

        EncounterBundlesData encountersForPatient = new EncounterService(freeSHR).getEncountersForPatient
                (patientId, null);
        assertEquals(1, encountersForPatient.getEncounterBundleDataList().size());

    }
}
