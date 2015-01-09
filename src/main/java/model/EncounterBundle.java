package model;

import org.hl7.fhir.instance.model.Resource;

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
}
