package org.freeshr.journal.model;


import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Reference;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TestResult {
    private CodeableConcept type;
    private CodeableConcept name;
    private List<SHRObservation> results;
    private Reference performer;
    private Date date;

    public TestResult(CodeableConcept name, List<SHRObservation> shrObservations, CodeableConcept category, Reference performer, Date date) {
        this.name = name;
        this.performer = performer;
        this.date = date;
        this.results = new ArrayList<>(shrObservations);
        this.type = category;
    }


    public CodeableConcept getType() {
        return type;
    }

    public CodeableConcept getName() {
        return name;
    }

    public List<SHRObservation> getResults() {
        return new ArrayList<>(results);
    }

    public Reference getPerformer() {
        return performer;
    }

    public Date getDate() {
        return date;
    }
}
