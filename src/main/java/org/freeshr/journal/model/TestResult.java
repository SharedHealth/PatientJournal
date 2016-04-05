package org.freeshr.journal.model;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;

import java.util.ArrayList;
import java.util.List;

public class TestResult {
    private CodeableConceptDt type;
    private CodeableConceptDt name;
    private List<SHRObservation> results;
    private ResourceReferenceDt performer;

    public TestResult(CodeableConceptDt name, List<SHRObservation> shrObservations, CodeableConceptDt category, ResourceReferenceDt performer) {
        this.name = name;
        this.performer = performer;
        this.results = new ArrayList<>(shrObservations);
        this.type = category;
    }


    public CodeableConceptDt getType() {
        return type;
    }

    public CodeableConceptDt getName() {
        return name;
    }

    public List<SHRObservation> getResults() {
        return new ArrayList<>(results);
    }

    public ResourceReferenceDt getPerformer() {
        return performer;
    }
}
