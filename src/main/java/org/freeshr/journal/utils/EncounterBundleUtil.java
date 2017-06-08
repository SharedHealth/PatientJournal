package org.freeshr.journal.utils;

import org.freeshr.journal.model.EncounterBundle;
import org.hl7.fhir.dstu3.model.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class EncounterBundleUtil {
    public static List<Resource> identifyTopLevelResourcesByExclusion(EncounterBundle bundle) {
        List<Resource> allResources = bundle.getResources();
        HashSet<Reference> childRef = getChildReferences(allResources);

        List<Resource> topLevelResources = new ArrayList<>();

        for (Resource resource : allResources) {
            if (resource instanceof Composition || resource instanceof Encounter) continue;
            if (!isChildReference(childRef, resource.getId())) {
                topLevelResources.add(resource);
            }
        }
        return topLevelResources;
    }

    private static HashSet<Reference> getChildReferences(List<Resource> allResources) {
        List<Reference> childResourceReferences = new ArrayList<>();
        for (Resource compositionRefResource : allResources) {
            // add all observation as part of observation target
            // add all observation as part of diagnosticreport result
            // add all diagnostic reports as part of procedure.report
            // add all medication requests as part of medicationrequest.priorprescription
            if (compositionRefResource instanceof DiagnosticReport) {
                DiagnosticReport diagnosticReport = (DiagnosticReport) compositionRefResource;
                childResourceReferences.addAll(diagnosticReport.getResult());
            }
            if (compositionRefResource instanceof MedicationRequest) {
                MedicationRequest medicationRequest = (MedicationRequest) compositionRefResource;
                Reference priorPrescription = medicationRequest.getPriorPrescription();
                if (!priorPrescription.isEmpty()) {
                    childResourceReferences.add(priorPrescription);
                }
            }
            if (compositionRefResource instanceof Procedure) {
                Procedure procedure = (Procedure) compositionRefResource;
                childResourceReferences.addAll(procedure.getReport());
            }
            if (compositionRefResource instanceof Observation) {
                List<Observation.ObservationRelatedComponent> related = ((Observation) compositionRefResource).getRelated();
                for (Observation.ObservationRelatedComponent observationRelatedComponent : related) {
                    childResourceReferences.add(observationRelatedComponent.getTarget());
                }
            }
        }
        HashSet<Reference> childRef = new HashSet<>();
        childRef.addAll(childResourceReferences);
        return childRef;
    }

    private static boolean isChildReference(HashSet<Reference> childReferenceDts, String resourceRef) {
        for (Reference childRef : childReferenceDts) {
            if (!childRef.getReference().isEmpty() && childRef.getReference().equals(resourceRef)) {
                return true;
            }
        }
        return false;
    }
}
