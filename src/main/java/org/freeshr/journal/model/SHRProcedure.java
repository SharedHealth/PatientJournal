package org.freeshr.journal.model;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;

import java.util.ArrayList;
import java.util.List;

public class SHRProcedure {
    private IDatatype date;
    private CodeableConceptDt type;
    private CodeableConceptDt outcome;
    private List<CodeableConceptDt> followUp;
    private List<SHRProcedureReport> procedureReports;

    public SHRProcedure(IDatatype date, CodeableConceptDt type, CodeableConceptDt outcome, List<CodeableConceptDt> followUp) {
        this.date = date;
        this.type = type;
        this.outcome = outcome;
        this.followUp = new ArrayList<>(followUp);
        this.procedureReports = new ArrayList<>();
    }

    public void addReport(SHRProcedureReport report) {
        this.procedureReports.add(report);
    }

    public IDatatype getDate() {
        return date;
    }

    public CodeableConceptDt getType() {
        return type;
    }

    public CodeableConceptDt getOutcome() {
        return outcome;
    }

    public List<CodeableConceptDt> getFollowUp() {
        return new ArrayList<>(followUp);
    }

    public List<SHRProcedureReport> getProcedureReports() {
        return new ArrayList<>(procedureReports);
    }
}
