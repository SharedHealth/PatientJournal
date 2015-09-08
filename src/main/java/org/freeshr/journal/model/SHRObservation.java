package org.freeshr.journal.model;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;

import java.util.ArrayList;
import java.util.List;

public class SHRObservation {
    private CodeableConceptDt name;
    private IDatatype value;
    private CodeableConceptDt interpretation;
    private String comments;
    private int depth;

    private List<SHRObservation> children;
    public SHRObservation(int depth) {
        this.children = new ArrayList<>();
        this.depth = depth;
    }

    public void setName(CodeableConceptDt name) {
        this.name = name;
    }

    public void setValue(IDatatype value) {
        this.value = value;
    }

    public void setInterpretation(CodeableConceptDt interpretation) {
        this.interpretation = interpretation;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void addChild(SHRObservation shrObservation) {
        children.add(shrObservation);

    }

    public int getDepth() {
        return depth;
    }

    public List<SHRObservation> getChildren() {
        return new ArrayList<>(children);
    }

    public CodeableConceptDt getName() {
        return name;
    }

    public IDatatype getValue() {
        return value;
    }

    public CodeableConceptDt getInterpretation() {
        return interpretation;
    }

    public String getComments() {
        return comments;
    }
}
