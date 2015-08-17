package org.freeshr.journal.model;

import org.hl7.fhir.instance.model.CodeableConcept;
import org.hl7.fhir.instance.model.ResourceReference;
import org.hl7.fhir.instance.model.Specimen;

import java.util.List;

public class TestOrder {
    private CodeableConcept item;
    private ResourceReference orderer;
    private Specimen sample;

    public void setItem(CodeableConcept item) {
        this.item = item;
    }

    public CodeableConcept getItem() {
        return item;
    }

    public void setOrderer(ResourceReference orderer) {
        this.orderer = orderer;
    }

    public ResourceReference getOrderer() {
        return orderer;
    }

    public void setSample(Specimen sample) {
        this.sample = sample;
    }

    public Specimen getSample() {
        return sample;
    }
}
