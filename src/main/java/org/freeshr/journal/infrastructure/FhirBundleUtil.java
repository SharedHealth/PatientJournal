package org.freeshr.journal.infrastructure;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.Bundle;


public class FhirBundleUtil {
    private static FhirContext fhirContext = FhirContext.forDstu2();
    public static Bundle parseBundle(String content, String type) {
        if (type.equals("xml")) {
            return (Bundle) fhirContext.newXmlParser().parseResource(content);
        } else {
            return (Bundle) fhirContext.newJsonParser().parseResource(content);
        }
    }
}
