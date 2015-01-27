package org.freeshr.journal.model;

import org.hl7.fhir.instance.model.*;

import java.util.ArrayList;
import java.util.List;

public class EncounterBundleData {
    private EncounterBundle encounterBundle;

    public EncounterBundleData(EncounterBundle encounterBundle) {
        this.encounterBundle = encounterBundle;
    }

    public EncounterBundle getEncounterBundle() {
        return encounterBundle;
    }

    public List<Composition> getCompositions() {
        return getResourceByType(ResourceType.Composition);
    }

    public List<Observation> getObservations() {
        return getResourceByType(ResourceType.Observation);
    }

    public List<Encounter> getEncounters() {
        return getResourceByType(ResourceType.Encounter);
    }


    public List<Condition> getConditions() {
        return getResourceByType(ResourceType.Condition);
    }

    private <T extends Resource> List<T> getResourceByType(ResourceType resourceType) {
        List<T> resources = new ArrayList<>();
        for (Resource resource : encounterBundle.getResources()) {
            if(resource.getResourceType().equals(resourceType))
                resources.add((T)resource);
        }
        return resources;
    }
}
