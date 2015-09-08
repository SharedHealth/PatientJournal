package org.freeshr.journal.model;

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.BoundCodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.*;
import ca.uhn.fhir.model.dstu2.valueset.ObservationInterpretationCodesEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
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
        return getResourceByType(Composition.class);
    }

    public List<Observation> getObservations() {
        return getResourceByType(Observation.class);
    }

    public List<Encounter> getEncounters() {
        return getResourceByType(Encounter.class);
    }


    public List<Condition> getConditions() {
        return getResourceByType(Condition.class);
    }

    public List<FamilyMemberHistory> getFamilyMemberHistories() {
        return getResourceByType(FamilyMemberHistory.class);
    }

    public List<MedicationPrescription> getMedicationPrescriptions() {
        return getResourceByType(MedicationPrescription.class);
    }

    public List<Immunization> getImmunizations() {
        return getResourceByType(Immunization.class);
    }

    public List<Condition> getDiagnosisConditions() {
        List<Condition> resourceByType = getResourceByType(Condition.class);
        List<Condition> diagnosis = new ArrayList<>();
        for (Condition condition : resourceByType) {
            if (condition.getCategory().getCoding().get(0).getCode().equals("diagnosis"))
                diagnosis.add(condition);
        }
        return diagnosis;
    }

    public List<Condition> getComplaintConditions() {
        List<Condition> resourceByType = getResourceByType(Condition.class);
        List<Condition> complaint = new ArrayList<>();
        for (Condition condition : resourceByType) {
            if (condition.getCategory().getCoding().get(0).getCode().equals("complaint"))
                complaint.add(condition);
        }
        return complaint;
    }

    public List<Condition> getFindingConditions() {
        List<Condition> resourceByType = getResourceByType(Condition.class);
        List<Condition> finding = new ArrayList<>();
        for (Condition condition : resourceByType) {
            if (condition.getCategory().getCoding().get(0).getCode().equals("finding"))
                finding.add(condition);
        }
        return finding;
    }

    public List<Condition> getSymptomConditions() {
        List<Condition> resourceByType = getResourceByType(Condition.class);
        List<Condition> symptom = new ArrayList<>();
        for (Condition condition : resourceByType) {
            if (condition.getCategory().getCoding().get(0).getCode().equals("symptom"))
                symptom.add(condition);
        }
        return symptom;
    }

    public List<TestOrder> getTestOrders() {
        List<DiagnosticOrder> diagnosticOrders = getResourceByType(DiagnosticOrder.class);
        return getAllTestOrdersInDiagnosticOrders(diagnosticOrders);
    }

    private ArrayList<TestOrder> getAllTestOrdersInDiagnosticOrders(List<DiagnosticOrder> diagnosticOrders) {
        ArrayList<TestOrder> testOrders = new ArrayList<>();
        for (DiagnosticOrder diagnosticOrder : diagnosticOrders) {
            for (DiagnosticOrder.Item item : diagnosticOrder.getItem()) {
                testOrders.add(getTestDetails(diagnosticOrder, item));
            }
        }
        return testOrders;
    }

    private TestOrder getTestDetails(DiagnosticOrder diagnosticOrder, DiagnosticOrder.Item item) {
        TestOrder testOrder = new TestOrder();
        testOrder.setItem(item.getCode());
        testOrder.setOrderer(diagnosticOrder.getOrderer());
        List<ResourceReferenceDt> specimens = item.getSpecimen();
        setSpecimen(testOrder, specimens);
        return testOrder;
    }

    private void setSpecimen(TestOrder testOrder, List<ResourceReferenceDt> specimens) {
        if (!specimens.isEmpty()) {
            for (IResource resource : encounterBundle.getResources()) {
                if (resource.getId().equals(specimens.get(0).getReference())) {
                    testOrder.setSample((Specimen) resource);
                }
            }
        }
    }

    public List<Procedure> getProcedures() {
        return getResourceByType(Procedure.class);
    }

    private <T extends IResource> List<T> getResourceByType(Class<T> type) {
        List<T> resources = new ArrayList<>();
        for (IResource resource : encounterBundle.getResources()) {
            if (type.isInstance(resource))
                resources.add((T) resource);
        }
        return resources;
    }

    public List<SHRObservation> getSHRObservations() {
        List<Observation> topLevelObs = identifyTopLevelResourcesOfTypeByExclusion(Observation.class);
        ArrayList<SHRObservation> shrObservations = new ArrayList<>();
        List<Observation> allObs = this.getObservations();
        for (Observation topLevelOb : topLevelObs) {
            int depth = 0;
            SHRObservation shrObservation = createShrObservation(topLevelOb, allObs, depth);
            shrObservations.add(shrObservation);
        }
        return shrObservations;
    }

    private SHRObservation createShrObservation(Observation observation, List<Observation> allObs, int depth) {
        SHRObservation shrObservation = new SHRObservation(depth);
        shrObservation.setName(observation.getCode());
        if (observation.getValue() != null) shrObservation.setValue(observation.getValue());
        if (observation.getComments() != null) shrObservation.setComments(observation.getComments());
        if (hasInterpretation(observation))
            shrObservation.setInterpretation(observation.getInterpretation());
        if (observation.getRelated().isEmpty()) return shrObservation;
        depth++;
        for (Observation.Related related : observation.getRelated()) {
            IdDt reference = related.getTarget().getReference();
            if (reference == null) continue;
            Observation childObservation = findChildObservation(reference.getValue(), allObs);
            
            if (childObservation != null) {
                SHRObservation childShrObservation = createShrObservation(childObservation, allObs, depth);
                shrObservation.addChild(childShrObservation);
            }
        }
        return shrObservation;
    }

    private boolean hasInterpretation(Observation observation) {
        BoundCodeableConceptDt<ObservationInterpretationCodesEnum> interpretation = observation.getInterpretation();
        if (interpretation == null) return false;
        if (!CollectionUtils.isEmpty(interpretation.getCoding())) return true;
        return interpretation.getText() != null;
    }

    private <T extends IResource> List<T> identifyTopLevelResourcesOfTypeByExclusion(Class<T> type) {
        List<IResource> allResources = encounterBundle.getResources();
        List<ResourceReferenceDt> childResourceReferences = new ArrayList<>();
        for (IResource resource : allResources) {
            if (resource instanceof Composition || resource instanceof Encounter) continue;
            childResourceReferences.addAll(resource.getAllPopulatedChildElementsOfType(ResourceReferenceDt.class));
        }
        HashSet<ResourceReferenceDt> childReferences = new HashSet<>();
        childReferences.addAll(childResourceReferences);

        ArrayList<T> topLevelResources = new ArrayList<>();

        for (IResource resource : allResources) {
            if (type.isInstance(resource)) {
                if (!isChildReference(childReferences, resource.getId().getValue())) {
                    topLevelResources.add((T) resource);
                }
            }
        }
        return topLevelResources;
    }

    private boolean isChildReference(HashSet<ResourceReferenceDt> childReferenceDts, String resourceRef) {
        for (ResourceReferenceDt childRef : childReferenceDts) {
            if (!childRef.getReference().isEmpty() && childRef.getReference().getValue().equals(resourceRef)) {
                return true;
            }
        }
        return false;
    }

    private Observation findChildObservation(String referenceSimple, List<Observation> allObs) {
        for (Observation observation : allObs) {
            if (referenceSimple.equals(observation.getId().getValue()))
                return observation;
        }
        return null;
    }

}
