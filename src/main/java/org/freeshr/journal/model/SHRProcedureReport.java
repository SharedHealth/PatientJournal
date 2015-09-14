package org.freeshr.journal.model;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;

import java.util.ArrayList;
import java.util.List;

public class SHRProcedureReport {
    private CodeableConceptDt test;
    private ResourceReferenceDt providerReference;
    private List<CodeableConceptDt> results;
    private List<SHRObservation> resultNotes;

    public SHRProcedureReport(CodeableConceptDt test, ResourceReferenceDt providerReference, List<CodeableConceptDt> results, List<SHRObservation> resultNotes) {
        this.test = test;
        this.providerReference = providerReference;
        this.results = new ArrayList<>(results);
        this.resultNotes = new ArrayList<>(resultNotes);
    }

    public CodeableConceptDt getTest() {
        return test;
    }

    public ResourceReferenceDt getProviderReference() {
        return providerReference;
    }

    public List<CodeableConceptDt> getResults() {
        return new ArrayList<>(results);
    }

    public List<SHRObservation> getResultNotes() {
        return resultNotes;
    }
}