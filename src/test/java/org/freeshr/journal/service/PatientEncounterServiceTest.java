package org.freeshr.journal.service;

import org.freeshr.journal.proxy.FreeSHR;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

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
//        when(freeSHR.getEncountersForPatient(patientId)).then

    }
}