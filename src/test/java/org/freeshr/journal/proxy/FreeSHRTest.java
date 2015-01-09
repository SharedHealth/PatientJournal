package org.freeshr.journal.proxy;

import infrastructure.AtomFeed;
import model.EncounterBundles;
import org.hl7.fhir.instance.model.Encounter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.MockitoAnnotations.initMocks;

public class FreeSHRTest {
    @Mock
    EncounterBundles encounterBundles;

    @Mock
    AtomFeed atomFeed;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void testGetEncountersForPatient() {
        FreeSHR freeSHR = new FreeSHR(encounterBundles, atomFeed);
        List<Encounter> encounters = freeSHR.getEncountersForPatient("123123123123");
    }
}