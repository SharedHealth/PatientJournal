package org.freeshr.journal.model;


import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Specimen;
import org.hl7.fhir.instance.model.api.IBaseDatatype;

public class TestOrder {
    private CodeableConceptDt item;
    private ResourceReferenceDt orderer;
    private Specimen sample;
    private IBaseDatatype type;

    public IBaseDatatype getType() {
        return type;
    }

    public void setType(IBaseDatatype type) {
        this.type = type;
    }

    public void setItem(CodeableConceptDt item) {
        this.item = item;
    }

    public CodeableConceptDt getItem() {
        return item;
    }

    public void setOrderer(ResourceReferenceDt orderer) {
        this.orderer = orderer;
    }

    public ResourceReferenceDt getOrderer() {
        return orderer;
    }

    public void setSample(Specimen sample) {
        this.sample = sample;
    }

    public Specimen getSample() {
        return sample;
    }
}
