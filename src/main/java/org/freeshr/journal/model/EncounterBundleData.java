package org.freeshr.journal.model;

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.*;
import ca.uhn.fhir.model.primitive.IdDt;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static org.freeshr.journal.utils.EncounterBundleUtil.identifyTopLevelResourcesOfTypeByExclusion;

public class EncounterBundleData {
    private EncounterBundle encounterBundle;
    private List<IResource> topLevelResources;

    public EncounterBundleData(EncounterBundle encounterBundle) {
        this.encounterBundle = encounterBundle;
        this.topLevelResources = identifyTopLevelResourcesOfTypeByExclusion(encounterBundle);
    }

    public EncounterBundle getEncounterBundle() {
        return encounterBundle;
    }

    public List<Composition> getCompositions() {
        return encounterBundle.getCompositions();
    }

    public List<Encounter> getEncounters() {
        return encounterBundle.getEncounters();
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
        return getConditionsOfCategory("diagnosis");
    }

    public List<Condition> getComplaintConditions() {
        return getConditionsOfCategory("complaint");
    }

    public List<Condition> getFindingConditions() {
        return getConditionsOfCategory("finding");
    }

    public List<Condition> getSymptomConditions() {
        return getConditionsOfCategory("symptom");
    }

    public List<TestOrder> getTestOrders() {
        List<DiagnosticOrder> diagnosticOrders = getResourceByType(DiagnosticOrder.class);
        return getAllTestOrdersInDiagnosticOrders(diagnosticOrders);
    }

    public List<SHRObservation> getSHRObservations() {
        List<Observation> topLevelObs = getResourceByType(Observation.class);
        List<SHRObservation> shrObservations = new ArrayList<>();
        for (Observation topLevelOb : topLevelObs) {
            int depth = 0;
            SHRObservation shrObservation = convertToSHRObservation(topLevelOb, depth);
            shrObservations.add(shrObservation);
        }
        return shrObservations;
    }

    public List<TestResult> getTestResults() {
        List<DiagnosticReport> diagnosticReports = getResourceByType(DiagnosticReport.class);
        List<TestResult> testResults = new ArrayList<>();
        for (DiagnosticReport diagnosticReport : diagnosticReports) {
            List<SHRObservation> shrObservations = extractSHRObservationsFromDiagnosisReport(diagnosticReport);
            TestResult testResult = new TestResult(diagnosticReport.getName(), shrObservations);
            testResults.add(testResult);
        }
        return testResults;
    }

    public List<SHRProcedure> getSHRProcedures() {
        List<Procedure> procedures = getResourceByType(Procedure.class);
        List<SHRProcedure> shrProcedures = new ArrayList<>();
        for (Procedure procedure : procedures) {
            CodeableConceptDt outcome = hasValue(procedure.getOutcome()) ? procedure.getOutcome() : null;
            SHRProcedure shrProcedure = new SHRProcedure(procedure.getPerformed(), procedure.getType(), outcome, procedure.getFollowUp());
            for (ResourceReferenceDt resourceReferenceDt : procedure.getReport()) {
                IResource resourceByReference = getResourceByReference(resourceReferenceDt.getReference());
                if ((null == resourceByReference) || !(resourceByReference instanceof DiagnosticReport)) continue;
                DiagnosticReport diagnosticReport = (DiagnosticReport) resourceByReference;
                List<SHRObservation> resultNotes = extractSHRObservationsFromDiagnosisReport(diagnosticReport);
                SHRProcedureReport report = new SHRProcedureReport(diagnosticReport.getName(), diagnosticReport.getPerformer(), diagnosticReport.getCodedDiagnosis(), resultNotes);
                shrProcedure.addReport(report);
            }
            shrProcedures.add(shrProcedure);
        }
        return shrProcedures;
    }

    private List<Condition> getConditionsOfCategory(String category) {
        List<Condition> resourceByType = getResourceByType(Condition.class);
        List<Condition> diagnosis = new ArrayList<>();
        for (Condition condition : resourceByType) {
            if (condition.getCategory().getCoding().get(0).getCode().equals(category))
                diagnosis.add(condition);
        }
        return diagnosis;
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
            IdDt reference = specimens.get(0).getReference();
            IResource resourceByReference = getResourceByReference(reference);
            if ((null == resourceByReference) || !(resourceByReference instanceof Specimen)) return;
            Specimen specimen = (Specimen) resourceByReference;
            if (specimen != null)
                testOrder.setSample(specimen);
        }
    }

    private <T extends IResource> List<T> getResourceByType(Class<T> type) {
        List<T> resources = new ArrayList<>();
        for (IResource resource : topLevelResources) {
            if (type.isInstance(resource))
                resources.add((T) resource);
        }
        return resources;
    }

    private SHRObservation convertToSHRObservation(Observation observation, int depth) {
        SHRObservation shrObservation = createSHRObservation(observation, depth);
        if (observation.getRelated().isEmpty()) return shrObservation;
        depth++;
        for (Observation.Related related : observation.getRelated()) {
            IdDt reference = related.getTarget().getReference();
            if (reference == null) continue;
            IResource resourceByReference = getResourceByReference(reference);
            if ((null == resourceByReference) || !(resourceByReference instanceof Observation)) continue;
            Observation childObservation = (Observation) resourceByReference;

            if (childObservation != null) {
                SHRObservation childShrObservation = convertToSHRObservation(childObservation, depth);
                shrObservation.addChild(childShrObservation);
            }
        }
        return shrObservation;
    }

    private SHRObservation createSHRObservation(Observation observation, int depth) {
        SHRObservation shrObservation = new SHRObservation(depth);
        shrObservation.setName(observation.getCode());
        if (observation.getValue() != null) shrObservation.setValue(observation.getValue());
        if (observation.getComments() != null) shrObservation.setComments(observation.getComments());
        if (hasValue(observation.getInterpretation()))
            shrObservation.setInterpretation(observation.getInterpretation());
        return shrObservation;
    }

    private boolean hasValue(CodeableConceptDt interpretation) {
        if (interpretation == null) return false;
        if (!CollectionUtils.isEmpty(interpretation.getCoding())) return true;
        return interpretation.getText() != null;
    }

    private List<SHRObservation> extractSHRObservationsFromDiagnosisReport(DiagnosticReport diagnosticReport) {
        List<SHRObservation> shrObservations = new ArrayList<>();
        List<ResourceReferenceDt> resultRefs = diagnosticReport.getResult();
        for (ResourceReferenceDt resultRef : resultRefs) {
            IResource resourceByReference = getResourceByReference(resultRef.getReference());
            if ((null == resourceByReference) || !(resourceByReference instanceof Observation)) continue;
            Observation observation = (Observation) resourceByReference;
            SHRObservation shrObservation = convertToSHRObservation(observation, 0);
            if (shrObservation != null)
                shrObservations.add(shrObservation);
        }
        return shrObservations;
    }

    private IResource getResourceByReference(IdDt reference) {
        for (IResource resource : encounterBundle.getResources()) {
            if (resource.getId().equals(reference)) {
                return resource;
            }
        }
        return null;
    }
}
