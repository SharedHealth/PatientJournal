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

    public List<FamilyHistory> getFamilyHistories() {
        return getResourceByType(ResourceType.FamilyHistory);
    }
    
    public List<MedicationPrescription> getMedicationPrescriptions() {
        return getResourceByType(ResourceType.MedicationPrescription);
    }

    public List<Immunization> getImmunizations() {
        return getResourceByType(ResourceType.Immunization);
    }

    public List<Condition> getDiagnosisConditions() {
        List<Condition> resourceByType = getResourceByType(ResourceType.Condition);
        List<Condition> diagnosis = new ArrayList<>();
        for (Condition condition : resourceByType) {
            if(condition.getCategory().getCoding().get(0).getCodeSimple().equals("diagnosis"))
                diagnosis.add(condition);
        }
        return diagnosis;
    }

    public List<Condition> getComplaintConditions() {
        List<Condition> resourceByType = getResourceByType(ResourceType.Condition);
        List<Condition> complaint = new ArrayList<>();
        for (Condition condition : resourceByType) {
            if(condition.getCategory().getCoding().get(0).getCodeSimple().equals("complaint"))
                complaint.add(condition);
        }
        return complaint;
    }

    public List<Condition> getFindingConditions() {
        List<Condition> resourceByType = getResourceByType(ResourceType.Condition);
        List<Condition> finding = new ArrayList<>();
        for (Condition condition : resourceByType) {
            if(condition.getCategory().getCoding().get(0).getCodeSimple().equals("finding"))
                finding.add(condition);
        }
        return finding;
    }

    public List<Condition> getSymptomConditions() {
        List<Condition> resourceByType = getResourceByType(ResourceType.Condition);
        List<Condition> symptom = new ArrayList<>();
        for (Condition condition : resourceByType) {
            if(condition.getCategory().getCoding().get(0).getCodeSimple().equals("symptom"))
                symptom.add(condition);
        }
        return symptom;
    }

    public List<TestOrder> getTestOrders() {
        List<DiagnosticOrder> diagnosticOrders = getResourceByType(ResourceType.DiagnosticOrder);
        ArrayList<TestOrder> testOrders = new ArrayList<>();
        for (DiagnosticOrder diagnosticOrder : diagnosticOrders) {
            for (DiagnosticOrder.DiagnosticOrderItemComponent diagnosticOrderItemComponent : diagnosticOrder.getItem()) {
                TestOrder testOrder = new TestOrder();
                testOrder.setItem(diagnosticOrderItemComponent.getCode());
                testOrder.setOrderer(diagnosticOrder.getOrderer());
                List<ResourceReference> specimenReference = diagnosticOrderItemComponent.getSpecimen();
                Specimen specimen = getResourceByReference(specimenReference.get(0));
                testOrder.setSample(specimen);
                testOrders.add(testOrder);
            }
        }
        return testOrders;
    }

    private <T extends Resource> List<T> getResourceByType(ResourceType resourceType) {
        List<T> resources = new ArrayList<>();
        for (Resource resource : encounterBundle.getResources()) {
            if(resource.getResourceType().equals(resourceType))
                resources.add((T)resource);
        }
        return resources;
    }

    private <T extends Resource> T getResourceByReference(ResourceReference resourceReference) {
        List<T> resources = new ArrayList<>();
        for (AtomEntry<? extends Resource> atomEntry : encounterBundle.getEncounterFeed().getEntryList()) {
          if (resourceReference.getReferenceSimple().equals(atomEntry.getId()))
              return (T) atomEntry.getResource();
        }
        return null;
    }
}
