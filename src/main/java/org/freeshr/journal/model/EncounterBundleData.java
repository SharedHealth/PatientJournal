package org.freeshr.journal.model;

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.*;

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
        if (!specimens.isEmpty()){
            for (IResource resource : encounterBundle.getResources()) {
                if(resource.getId().equals(specimens.get(0).getReference())){
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
}
