package org.freeshr.journal.model;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;

import java.util.ArrayList;
import java.util.List;

public class TestResult {
    private CodeableConceptDt name;
    private List<IDatatype> results;

    public TestResult(CodeableConceptDt name) {
        this.name = name;
        this.results = new ArrayList<>();
    }

    public void addResult(IDatatype result) {
        this.results.add(result);
    }

    public CodeableConceptDt getName() {
        return name;
    }

    public List<IDatatype> getResults() {
        return new ArrayList<>(results);
    }
}
