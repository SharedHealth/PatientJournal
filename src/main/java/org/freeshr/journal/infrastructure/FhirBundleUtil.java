package org.freeshr.journal.infrastructure;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.Bundle;
import ca.uhn.fhir.model.dstu2.resource.Patient;
import org.hl7.fhir.instance.model.api.IBaseResource;


public class FhirBundleUtil {
    private static FhirContext fhirContext = FhirContext.forDstu2();

    public static Bundle parseBundle(String content, String type) {
        if (type.equals("xml")) {
            return (Bundle) fhirContext.newXmlParser().parseResource(content);
        } else {
            return (Bundle) fhirContext.newJsonParser().parseResource(content);
        }
    }

    public static IBaseResource parseResource(String content) {
        return fhirContext.newXmlParser().parseResource(content);
    }

    public static Patient findPatientResource(String bundleString) {
        Bundle bundle = (Bundle) parseResource(bundleString);
        for (Bundle.Entry entry : bundle.getEntry()) {
            if (new Patient().getResourceName().equals(entry.getResource().getResourceName())) {
                return (Patient) entry.getResource();
            }
        }
        return null;
    }
}
