package org.freeshr.journal.model;


import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Reference;

import java.util.Date;

public class ProcedureOrder {
    private Date date;
    private Reference orderer;
    private String notes;
    private CodeableConcept type;
    private String status;
    private Reference facility;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Reference getOrderer() {
        return orderer;
    }

    public void setOrderer(Reference orderer) {
        this.orderer = orderer;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public CodeableConcept getType() {
        return type;
    }

    public void setType(CodeableConcept type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Reference getFacility() {
        return facility;
    }

    public void setFacility(Reference facility) {
        this.facility = facility;
    }
}
