package org.freeshr.journal.model;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;

import java.util.Date;

public class ProcedureOrder {

    private Date date;
    private ResourceReferenceDt orderer;
    private String notes;
    private CodeableConceptDt type;
    private String status;
    private ResourceReferenceDt facility;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public ResourceReferenceDt getOrderer() {
        return orderer;
    }

    public void setOrderer(ResourceReferenceDt orderer) {
        this.orderer = orderer;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public CodeableConceptDt getType() {
        return type;
    }

    public void setType(CodeableConceptDt type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ResourceReferenceDt getFacility() {
        return facility;
    }

    public void setFacility(ResourceReferenceDt facility) {
        this.facility = facility;
    }
}
