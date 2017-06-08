package org.freeshr.journal.model;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Reference;

import java.util.ArrayList;
import java.util.List;

public class SHRProcedureReport {
    private CodeableConcept test;
    private Reference providerReference;
    private List<CodeableConcept> results;
    private List<SHRObservation> resultNotes;

    public SHRProcedureReport(CodeableConcept test, Reference providerReference, List<CodeableConcept> results, List<SHRObservation> resultNotes) {
        this.test = test;
        this.providerReference = providerReference;
        this.results = new ArrayList<>(results);
        this.resultNotes = new ArrayList<>(resultNotes);
    }

    public CodeableConcept getTest() {
        return test;
    }

    public Reference getProviderReference() {
        return providerReference;
    }

    public List<CodeableConcept> getResults() {
        return new ArrayList<>(results);
    }

    public List<SHRObservation> getResultNotes() {
        return resultNotes;
    }
}
