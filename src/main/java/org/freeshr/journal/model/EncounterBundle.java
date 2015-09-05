package org.freeshr.journal.model;

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.resource.Bundle;

import java.util.ArrayList;
import java.util.List;

public class EncounterBundle {
    private List<IResource> resources = new ArrayList<>();
    private Bundle bundle;

    public void addResource(IResource resource) {
        resources.add(resource);
    }

    public List<IResource> getResources() {
        return new ArrayList<>(resources);
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public Bundle getBundle() {
        return bundle;
    }
}
