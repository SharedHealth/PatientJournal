package org.freeshr.journal.utils;


import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;
import org.freeshr.journal.model.EncounterBundle;
import org.junit.Test;

import java.util.List;

import static org.freeshr.journal.infrastructure.FhirBundleUtil.parseBundle;
import static org.freeshr.journal.utils.EncounterBundleUtil.identifyTopLevelResourcesOfTypeByExclusion;
import static org.freeshr.journal.utils.FileUtil.asString;
import static org.junit.Assert.assertEquals;

public class EncounterBundleUtilTest {
    @Test
    public void shouldIdentifyTopLevelDiagnosticReports() throws Exception {
        String content = asString("encounters/bundleWithDiagnosticReport.xml");
        Bundle bundle = parseBundle(content, "xml");
        EncounterBundle encounterBundle = new EncounterBundle();
        encounterBundle.setBundle(bundle);
        for (Bundle.Entry bundleEntry : bundle.getEntry()) {
            encounterBundle.addResource(bundleEntry.getResource());
        }

        List<IResource> diagnosticReports = identifyTopLevelResourcesOfTypeByExclusion(encounterBundle, IResource.class);
        assertEquals(2, diagnosticReports.size());
    }
}