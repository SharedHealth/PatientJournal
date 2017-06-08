package org.freeshr.journal.model;

import org.hl7.fhir.dstu3.model.CodeableConcept;
import org.hl7.fhir.dstu3.model.Type;

import java.util.ArrayList;
import java.util.List;

public class SHRObservation {
    private CodeableConcept name;
    private Type value;
    private CodeableConcept interpretation;
    private String comments;
    private int depth;

    private List<SHRObservation> children;
    public SHRObservation(int depth) {
        this.children = new ArrayList<>();
        this.depth = depth;
    }

    public void setName(CodeableConcept name) {
        this.name = name;
    }

    public void setValue(Type value) {
        this.value = value;
    }

    public void setInterpretation(CodeableConcept interpretation) {
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

    public CodeableConcept getName() {
        return name;
    }

    public Type getValue() {
        return value;
    }

    public CodeableConcept getInterpretation() {
        return interpretation;
    }

    public String getComments() {
        return comments;
    }
}
