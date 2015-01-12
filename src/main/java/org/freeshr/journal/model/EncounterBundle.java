package org.freeshr.journal.model;

import org.hl7.fhir.instance.model.Encounter;
import org.hl7.fhir.instance.model.Resource;
import org.hl7.fhir.instance.model.ResourceType;

import java.util.ArrayList;
import java.util.List;

public class EncounterBundle {
    private List<Resource> resources = new ArrayList<>();

    public void addResource(Resource resource) {
        resources.add(resource);
    }

    public List<Resource> getResources() {
        return new ArrayList<>(resources);
    }

    public Encounter getEncounter() {
        for (Resource resource : resources) {
            if(resource.getResourceType() == ResourceType.Encounter)
                return (Encounter) resource;
        }
        return null;
    }
}
