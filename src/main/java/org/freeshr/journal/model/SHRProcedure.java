package org.freeshr.journal.model;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Type;

import java.util.ArrayList;
import java.util.List;

public class SHRProcedure {
    private Type date;
    private CodeableConcept type;
    private CodeableConcept outcome;
    private List<CodeableConcept> followUp;
    private List<SHRProcedureReport> procedureReports;

    public SHRProcedure(Type date, CodeableConcept type, CodeableConcept outcome, List<CodeableConcept> followUp) {
        this.date = date;
        this.type = type;
        this.outcome = outcome;
        this.followUp = new ArrayList<>(followUp);
        this.procedureReports = new ArrayList<>();
    }

    public void addReport(SHRProcedureReport report) {
        this.procedureReports.add(report);
    }

    public Type getDate() {
        return date;
    }

    public CodeableConcept getType() {
        return type;
    }

    public CodeableConcept getOutcome() {
        return outcome;
    }

    public List<CodeableConcept> getFollowUp() {
        return new ArrayList<>(followUp);
    }

    public List<SHRProcedureReport> getProcedureReports() {
        return new ArrayList<>(procedureReports);
    }
}
