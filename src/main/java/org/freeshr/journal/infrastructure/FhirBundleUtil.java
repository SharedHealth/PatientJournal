package org.freeshr.journal.infrastructure;

import ca.uhn.fhir.context.FhirContext;
import org.hl7.fhir.dstu3.model.Bundle;


public class FhirBundleUtil {
    private static FhirContext fhirContext = FhirContext.forDstu3();

    public static Bundle parseBundle(String content, String type) {
        if (type.equals("xml")) {
            return (Bundle) fhirContext.newXmlParser().parseResource(content);
        } else {
            return (Bundle) fhirContext.newJsonParser().parseResource(content);
        }
    }
}
