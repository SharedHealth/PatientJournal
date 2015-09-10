package org.freeshr.journal.utils;

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Composition;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import org.freeshr.journal.model.EncounterBundle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class EncounterBundleUtil {
    public static List<IResource> identifyTopLevelResourcesOfTypeByExclusion(EncounterBundle encounterBundle) {
        List<IResource> allResources = encounterBundle.getResources();
        List<ResourceReferenceDt> childResourceReferences = new ArrayList<>();
        for (IResource resource : allResources) {
            if (resource instanceof Composition || resource instanceof Encounter) continue;
            childResourceReferences.addAll(resource.getAllPopulatedChildElementsOfType(ResourceReferenceDt.class));
        }
        HashSet<ResourceReferenceDt> childReferences = new HashSet<>();
        childReferences.addAll(childResourceReferences);

        ArrayList<IResource> topLevelResources = new ArrayList<>();

        for (IResource resource : allResources) {
            if (resource instanceof Composition || resource instanceof Encounter) continue;
            if (!isChildReference(childReferences, resource.getId().getValue())) {
                topLevelResources.add(resource);
            }
        }
        return topLevelResources;
    }

    private static boolean isChildReference(HashSet<ResourceReferenceDt> childReferenceDts, String resourceRef) {
        for (ResourceReferenceDt childRef : childReferenceDts) {
            if (!childRef.getReference().isEmpty() && childRef.getReference().getValue().equals(resourceRef)) {
                return true;
            }
        }
        return false;
    }

}
