package org.freeshr.journal.model;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestResult {
    private CodeableConceptDt type;
    private CodeableConceptDt name;
    private List<SHRObservation> results;
    private ResourceReferenceDt performer;
    private Date date;

    public TestResult(CodeableConceptDt name, List<SHRObservation> shrObservations, CodeableConceptDt category, ResourceReferenceDt performer, Date date) {
        this.name = name;
        this.performer = performer;
        this.date = date;
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

    public Date getDate() {
        return date;
    }
}
