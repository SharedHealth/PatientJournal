package org.freeshr.journal.utils;


import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Composition;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import org.freeshr.journal.model.EncounterBundle;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.freeshr.journal.infrastructure.FhirBundleUtil.parseBundle;
import static org.freeshr.journal.utils.EncounterBundleUtil.identifyTopLevelResourcesByExclusion;
import static org.freeshr.journal.utils.FileUtil.asString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class EncounterBundleUtilTest {

    private EncounterBundle encounterBundle;

    @Before
    public void setUp() throws Exception {
        String content = asString("encounters/bundleWithDiagnosticReport.xml");
        Bundle bundle = parseBundle(content, "xml");
        encounterBundle = new EncounterBundle();
        encounterBundle.setBundle(bundle);
        for (Bundle.Entry bundleEntry : bundle.getEntry()) {
            encounterBundle.addResource(bundleEntry.getResource());
        }
    }

    @Test
    public void shouldIdentifyTopLevelDiagnosticReports() throws Exception {
        List<IResource> diagnosticReports = identifyTopLevelResourcesByExclusion(encounterBundle);
        assertEquals(4, diagnosticReports.size());
    }

    @Test
    public void shouldIdentifyTopLevelResourcesOtherThanEncounterAndComposition() throws Exception {
        List<IResource> resources = identifyTopLevelResourcesByExclusion(encounterBundle);
        for (IResource resource : resources) {
            assertFalse(isEncounterOrComposition(resource));
        }
    }

    private boolean isEncounterOrComposition(IResource resource) {
        return resource instanceof Encounter || resource instanceof Composition;
    }
}