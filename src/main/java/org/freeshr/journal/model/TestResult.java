package org.freeshr.journal.model;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;

import java.util.ArrayList;
import java.util.List;

public class TestResult {
    private CodeableConceptDt name;
    private List<SHRObservation> results;

    public TestResult(CodeableConceptDt name) {
        this.name = name;
        this.results = new ArrayList<>();
    }

    public void addResult(SHRObservation result) {
        this.results.add(result);
    }

    public CodeableConceptDt getName() {
        return name;
    }

    public List<SHRObservation> getResults() {
        return new ArrayList<>(results);
    }
}
