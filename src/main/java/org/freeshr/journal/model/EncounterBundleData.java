package org.freeshr.journal.model;

import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.AnnotationDt;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.CodingDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.*;
import ca.uhn.fhir.model.primitive.IdDt;
import ca.uhn.fhir.model.primitive.StringDt;
import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.instance.model.api.IBaseDatatype;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.freeshr.journal.utils.EncounterBundleUtil.identifyTopLevelResourcesByExclusion;

public class EncounterBundleData {
    private EncounterBundle encounterBundle;
    private List<IResource> topLevelResources;
    private String ordersExtensionUrl = "https://sharedhealth.atlassian.net/wiki/display/docs/fhir-extensions#DiagnositicOrderCategory";
    private String LAB_CATEGORY_DISPLAY = "Laboratory";
    private String LAB_EXTENSION_CODE = "LAB";

    public EncounterBundleData(EncounterBundle encounterBundle) {
        this.encounterBundle = encounterBundle;
        this.topLevelResources = identifyTopLevelResourcesByExclusion(encounterBundle);
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

    public List<MedicationOrder> getMedicationOrders() {
        return getResourceByType(MedicationOrder.class);
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
            CodeableConceptDt category = getCategoryFromReport(diagnosticReport);
            TestResult testResult = new TestResult(diagnosticReport.getCode(), shrObservations, category, diagnosticReport.getPerformer(), diagnosticReport.getIssued());
            testResults.add(testResult);
        }
        return testResults;
    }

    public List<SHRProcedure> getSHRProcedures() {
        List<Procedure> procedures = getResourceByType(Procedure.class);
        List<SHRProcedure> shrProcedures = new ArrayList<>();
        for (Procedure procedure : procedures) {
            CodeableConceptDt outcome = hasValue(procedure.getOutcome()) ? procedure.getOutcome() : null;
            SHRProcedure shrProcedure = new SHRProcedure(procedure.getPerformed(), procedure.getCode(), outcome, procedure.getFollowUp());
            for (ResourceReferenceDt resourceReferenceDt : procedure.getReport()) {
                IResource resourceByReference = getResourceByReference(resourceReferenceDt.getReference());
                if ((null == resourceByReference) || !(resourceByReference instanceof DiagnosticReport)) continue;
                DiagnosticReport diagnosticReport = (DiagnosticReport) resourceByReference;
                List<SHRObservation> resultNotes = extractSHRObservationsFromDiagnosisReport(diagnosticReport);
                SHRProcedureReport report = new SHRProcedureReport(diagnosticReport.getCode(), diagnosticReport.getPerformer(), diagnosticReport.getCodedDiagnosis(), resultNotes);
                shrProcedure.addReport(report);
            }
            shrProcedures.add(shrProcedure);
        }
        return shrProcedures;
    }

    public List<ProcedureOrder> getProcedureOrders() {

        List<ProcedureRequest> procedureRequests = getResourceByType(ProcedureRequest.class);
        List<ProcedureOrder> procedureOrders = new ArrayList<>();
        for (ProcedureRequest procedureRequest : procedureRequests) {
            ProcedureOrder procedureOrder = new ProcedureOrder();
            procedureOrder.setDate(procedureRequest.getOrderedOn());
            procedureOrder.setOrderer(procedureRequest.getOrderer());
            procedureOrder.setStatus(procedureRequest.getStatus());
            List<AnnotationDt> notes = procedureRequest.getNotes();
            if (!CollectionUtils.isEmpty(notes)) {
                procedureOrder.setNotes(notes.get(0).getText());
            }
            procedureOrder.setFacility(encounterBundle.getEncounters().get(0).getServiceProvider());
            procedureOrder.setType(procedureRequest.getCode());
            procedureOrders.add(procedureOrder);
        }
        return procedureOrders;
    }

    private CodeableConceptDt getCategoryFromReport(DiagnosticReport diagnosticReport) {
        CodeableConceptDt category = diagnosticReport.getCategory();
        if (category.isEmpty()) {
            CodingDt coding = new CodingDt().setDisplay(LAB_CATEGORY_DISPLAY);
            category.setCoding(asList(coding));
        }
        return category;
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
                TestOrder testDetails = getTestDetails(diagnosticOrder, item);
                if (null == testDetails) continue;
                testOrders.add(testDetails);
            }
        }
        return testOrders;
    }

    private TestOrder getTestDetails(DiagnosticOrder diagnosticOrder, DiagnosticOrder.Item item) {
        TestOrder testOrder = new TestOrder();
        testOrder.setItem(item.getCode());
        testOrder.setOrderer(diagnosticOrder.getOrderer());
        testOrder.setStatus(item.getStatus());
        testOrder.setDate(item.getEventFirstRep().getDateTime());
        List<ResourceReferenceDt> specimens = item.getSpecimen();
        setSpecimen(testOrder, specimens);

        IBaseDatatype extension = getExtensionDt(diagnosticOrder);

        testOrder.setType(extension);
        return testOrder;
    }

    private IBaseDatatype getExtensionDt(DiagnosticOrder diagnosticOrder) {
        if (!diagnosticOrder.getUndeclaredExtensionsByUrl(ordersExtensionUrl).isEmpty())
            return diagnosticOrder.getUndeclaredExtensionsByUrl(ordersExtensionUrl).get(0).getValue();
        return new StringDt(LAB_EXTENSION_CODE);
    }

    private void setSpecimen(TestOrder testOrder, List<ResourceReferenceDt> specimens) {
        if (!specimens.isEmpty()) {
            IdDt reference = specimens.get(0).getReference();
            IResource resourceByReference = getResourceByReference(reference);
            if ((null == resourceByReference) || !(resourceByReference instanceof Specimen)) return;
            Specimen specimen = (Specimen) resourceByReference;
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

            SHRObservation childShrObservation = convertToSHRObservation(childObservation, depth);
            shrObservation.addChild(childShrObservation);
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

    public IResource getResourceByReference(IdDt resourceReference) {
        for (Bundle.Entry entry : encounterBundle.getBundle().getEntry()) {
            IResource entryResource = entry.getResource();
            IdDt entryResourceId = entryResource.getId();
            boolean hasFullUrlDefined = !StringUtils.isBlank(entry.getFullUrl());

            if (resourceReference.hasResourceType() && entryResourceId.hasResourceType()
                    && entryResourceId.getValue().equals(resourceReference.getValue())) {
                return entryResource;
            } else if (entryResourceId.getIdPart().equals(resourceReference.getIdPart())) {
                return entryResource;
            } else if (hasFullUrlDefined) {
                if (entry.getFullUrl().endsWith(resourceReference.getIdPart())) {
                    return entryResource;
                }
            }
        }
        return null;
    }

}



