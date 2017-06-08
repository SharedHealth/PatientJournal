package org.freeshr.journal.utils;


import org.freeshr.journal.model.EncounterBundle;
import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Resource;
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
        String content = asString("encounters/stu3/bundleWithDiagnosticReport.xml");
        Bundle bundle = parseBundle(content, "xml");
        encounterBundle = new EncounterBundle();
        encounterBundle.setBundle(bundle);
        for (Bundle.BundleEntryComponent bundleEntry : bundle.getEntry()) {
            encounterBundle.addResource(bundleEntry.getResource());
        }
    }

    @Test
    public void shouldIdentifyTopLevelDiagnosticReports() throws Exception {
        List<Resource> diagnosticReports = identifyTopLevelResourcesByExclusion(encounterBundle);
        assertEquals(4, diagnosticReports.size());
    }

    @Test
    public void shouldIdentifyTopLevelResourcesOtherThanEncounterAndComposition() throws Exception {
        List<Resource> resources = identifyTopLevelResourcesByExclusion(encounterBundle);
        for (Resource resource : resources) {
            assertFalse(isEncounterOrComposition(resource));
        }
    }

    private boolean isEncounterOrComposition(Resource resource) {
        return resource instanceof Encounter || resource instanceof Composition;
    }
}
