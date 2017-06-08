package org.freeshr.journal.model;


import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Reference;
import org.hl7.fhir.dstu3.model.Specimen;

import java.util.Date;

public class Order {
    private Date date;
    private Reference orderer;
    private String notes;
    private String type;
    private Reference facility;
    private String status;
    private Specimen sample;
    private CodeableConcept code;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setOrderer(Reference orderer) {
        this.orderer = orderer;
    }

    public Reference getOrderer() {
        return orderer;
    }

    public void setSample(Specimen sample) {
        this.sample = sample;
    }

    public Specimen getSample() {
        return sample;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Reference getFacility() {
        return facility;
    }

    public void setFacility(Reference facility) {
        this.facility = facility;
    }

    public CodeableConcept getCode() {
        return code;
    }

    public void setCode(CodeableConcept code) {
        this.code = code;
    }
}
