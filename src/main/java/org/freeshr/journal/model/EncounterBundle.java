package org.freeshr.journal.model;

import org.hl7.fhir.dstu3.model.Bundle;
import org.hl7.fhir.dstu3.model.Composition;
import org.hl7.fhir.dstu3.model.Encounter;
import org.hl7.fhir.dstu3.model.Resource;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

public class EncounterBundle {
    private List<Resource> resources = new ArrayList<>();
    private Bundle bundle;
    private Composition composition;
    private Encounter encounter;

    public void addResource(Resource resource) {
        if (resource instanceof Composition) this.composition = (Composition) resource;
        if (resource instanceof Encounter) this.encounter = (Encounter) resource;
        resources.add(resource);
    }

    public List<Resource> getResources() {
        return new ArrayList<>(resources);
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public List<Composition> getCompositions() {
        return asList(composition);
    }

    public List<Encounter> getEncounters() {
        return asList(encounter);
    }
}
