package org.freeshr.journal.service;

import org.freeshr.journal.model.EncounterBundle;
import org.freeshr.journal.model.EncounterBundles;
import org.freeshr.journal.proxy.FreeSHR;
import org.hl7.fhir.instance.model.Encounter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class PatientEncounterServiceTest {
    @Mock
    FreeSHR freeSHR;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldGetEncounterFromSHR() throws Exception {
        String patientId = "5952907233237925889";
        EncounterBundles value = new EncounterBundles();
        EncounterBundle encounterBundle = new EncounterBundle();
        encounterBundle.addResource(new Encounter());
        value.addEncounterBundle(encounterBundle);
        when(freeSHR.getEncountersForPatient(patientId, null)).thenReturn(value);

        EncounterBundles encountersForPatient = new PatientEncounterService(freeSHR).getEncountersForPatient
                (patientId, null);
        assertEquals(1, encountersForPatient.getEncounterBundles().size());

    }
}