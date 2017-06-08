package org.freeshr.journal.model;

import org.apache.commons.lang3.StringUtils;
import org.hl7.fhir.dstu3.model.*;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.freeshr.journal.utils.EncounterBundleUtil.identifyTopLevelResourcesByExclusion;

public class EncounterBundleData {
    private EncounterBundle encounterBundle;
    private List<Resource> topLevelResources;
    private String ordersExtensionUrl = "https://sharedhealth.atlassian.net/wiki/display/docs/fhir-extensions#DiagnosticOrderCategory";
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

    public List<MedicationRequest> getMedicationRequests() {
        return getResourceByType(MedicationRequest.class);
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

    public List<Order> getOrders() {
        List<ProcedureRequest> procedureRequests = getResourceByType(ProcedureRequest.class);
        List<Order> orders = new ArrayList<>();
        for (ProcedureRequest procedureRequest : procedureRequests) {
            Order order = new Order();
            order.setDate(procedureRequest.getAuthoredOn());
            order.setFacility(encounterBundle.getEncounters().get(0).getServiceProvider());
            order.setOrderer(procedureRequest.getRequester().getAgent());
            List<Annotation> notes = procedureRequest.getNote();
            if (!CollectionUtils.isEmpty(notes)) {
                order.setNotes(notes.get(0).getText());
            }
            Resource resource = getResourceByReference(encounterBundle.getBundle(), procedureRequest.getSpecimenFirstRep());
            if (null != resource && resource instanceof Specimen) {
                order.setSample((Specimen) resource);
            }
            order.setStatus(procedureRequest.getStatus().toCode());
            String code = procedureRequest.getCategoryFirstRep().getCodingFirstRep().getCode();
            order.setType(code != null ? code : LAB_EXTENSION_CODE);
            order.setCode(procedureRequest.getCode());
            orders.add(order);
        }
        return orders;
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
            CodeableConcept category = getCategoryFromReport(diagnosticReport);
            TestResult testResult = new TestResult(diagnosticReport.getCode(), shrObservations, category, diagnosticReport.getPerformerFirstRep().getActor(), diagnosticReport.getIssued());
            testResults.add(testResult);
        }
        return testResults;
    }

    public List<SHRProcedure> getSHRProcedures() {
        List<Procedure> procedures = getResourceByType(Procedure.class);
        List<SHRProcedure> shrProcedures = new ArrayList<>();
        for (Procedure procedure : procedures) {
            CodeableConcept outcome = hasValue(procedure.getOutcome()) ? procedure.getOutcome() : null;
            SHRProcedure shrProcedure = new SHRProcedure(procedure.getPerformed(), procedure.getCode(), outcome, procedure.getFollowUp());
            for (Reference reference : procedure.getReport()) {
                Resource resourceByReference = getResourceByReference(encounterBundle.getBundle(), reference);
                if ((null == resourceByReference) || !(resourceByReference instanceof DiagnosticReport)) continue;
                DiagnosticReport diagnosticReport = (DiagnosticReport) resourceByReference;
                List<SHRObservation> resultNotes = extractSHRObservationsFromDiagnosisReport(diagnosticReport);
                SHRProcedureReport report = new SHRProcedureReport(diagnosticReport.getCode(), diagnosticReport.getPerformerFirstRep().getActor(), diagnosticReport.getCodedDiagnosis(), resultNotes);
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
            procedureOrder.setDate(procedureRequest.getAuthoredOn());
            procedureOrder.setOrderer(procedureRequest.getRequester().getAgent());
            procedureOrder.setStatus(procedureRequest.getStatus().toCode());
            List<Annotation> notes = procedureRequest.getNote();
            if (!CollectionUtils.isEmpty(notes)) {
                procedureOrder.setNotes(notes.get(0).getText());
            }
            procedureOrder.setFacility(encounterBundle.getEncounters().get(0).getServiceProvider());
            procedureOrder.setType(procedureRequest.getCode());
            procedureOrders.add(procedureOrder);
        }
        return procedureOrders;
    }

    private CodeableConcept getCategoryFromReport(DiagnosticReport diagnosticReport) {
        CodeableConcept category = diagnosticReport.getCategory();
        if (category.isEmpty()) {
            Coding coding = new Coding().setDisplay(LAB_CATEGORY_DISPLAY);
            category.setCoding(asList(coding));
        }
        return category;
    }

    private List<Condition> getConditionsOfCategory(String category) {
        List<Condition> resourceByType = getResourceByType(Condition.class);
        List<Condition> diagnosis = new ArrayList<>();
        for (Condition condition : resourceByType) {
            if (condition.getCategoryFirstRep().getCoding().get(0).getCode().equals(category))
                diagnosis.add(condition);
        }
        return diagnosis;
    }

//    private ArrayList<Order> getAllTestOrdersInDiagnosticOrders(List<DiagnosticOrder> diagnosticOrders) {
//        ArrayList<Order> testOrders = new ArrayList<>();
//        for (DiagnosticOrder diagnosticOrder : diagnosticOrders) {
//            for (DiagnosticOrder.Item item : diagnosticOrder.getItem()) {
//                Order testDetails = getTestDetails(diagnosticOrder, item);
//                if (null == testDetails) continue;
//                testOrders.add(testDetails);
//            }
//        }
//        return testOrders;
//    }

//    private Order getTestDetails(DiagnosticOrder diagnosticOrder, DiagnosticOrder.Item item) {
//        Order testOrder = new Order();
//        testOrder.setItem(item.getCode());
//        testOrder.setOrderer(diagnosticOrder.getOrderer());
//        testOrder.setStatus(item.getStatus());
//        testOrder.setDate(item.getEventFirstRep().getDateTime());
//        List<Reference> specimens = item.getSpecimen();
//        setSpecimen(testOrder, specimens);
//
//        IBaseDatatype extension = getExtensionDt(diagnosticOrder);
//
//        testOrder.setType(extension);
//        return testOrder;
//    }

//    private IBaseDatatype getExtensionDt(DiagnosticOrder diagnosticOrder) {
//        if (!diagnosticOrder.getUndeclaredExtensionsByUrl(ordersExtensionUrl).isEmpty())
//            return diagnosticOrder.getUndeclaredExtensionsByUrl(ordersExtensionUrl).get(0).getValue();
//        return new StringDt(LAB_EXTENSION_CODE);
//    }

//    private void setSpecimen(Order testOrder, List<Reference> specimens) {
//        if (!specimens.isEmpty()) {
//            Reference reference = specimens.get(0);
//            Resource resourceByReference = getResourceByReference(encounterBundle.getBundle(), reference);
//            if ((null == resourceByReference) || !(resourceByReference instanceof Specimen)) return;
//            Specimen specimen = (Specimen) resourceByReference;
//            testOrder.setSample(specimen);
//        }
//    }

    private <T extends Resource> List<T> getResourceByType(Class<T> type) {
        List<T> resources = new ArrayList<>();
        for (Resource resource : topLevelResources) {
            if (type.isInstance(resource))
                resources.add((T) resource);
        }
        return resources;
    }

    private SHRObservation convertToSHRObservation(Observation observation, int depth) {
        SHRObservation shrObservation = createSHRObservation(observation, depth);
        if (observation.getRelated().isEmpty()) return shrObservation;
        depth++;
        for (Observation.ObservationRelatedComponent related : observation.getRelated()) {
            Reference reference = related.getTarget();
            if (reference == null || reference.isEmpty()) continue;
            Resource resourceByReference = getResourceByReference(encounterBundle.getBundle(), reference);
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
        if (StringUtils.isNotBlank(observation.getComment())) shrObservation.setComments(observation.getComment());
        if (hasValue(observation.getInterpretation()))
            shrObservation.setInterpretation(observation.getInterpretation());
        return shrObservation;
    }

    private boolean hasValue(CodeableConcept interpretation) {
        if (interpretation == null) return false;
        if (!CollectionUtils.isEmpty(interpretation.getCoding())) return true;
        return interpretation.getText() != null;
    }

    private List<SHRObservation> extractSHRObservationsFromDiagnosisReport(DiagnosticReport diagnosticReport) {
        List<SHRObservation> shrObservations = new ArrayList<>();
        List<Reference> resultRefs = diagnosticReport.getResult();
        for (Reference resultRef : resultRefs) {
            Resource resourceByReference = getResourceByReference(encounterBundle.getBundle(), resultRef);
            if ((null == resourceByReference) || !(resourceByReference instanceof Observation)) continue;
            Observation observation = (Observation) resourceByReference;
            SHRObservation shrObservation = convertToSHRObservation(observation, 0);
            if (shrObservation != null)
                shrObservations.add(shrObservation);
        }
        return shrObservations;
    }

    public Resource getResourceByReference(Bundle bundle, Reference resourceRef) {
        if (resourceRef.isEmpty()) return null;
        String resourceReferenceIdPart = StringUtils.substringAfter(resourceRef.getReference(), "urn:uuid:");
        for (Bundle.BundleEntryComponent entry : bundle.getEntry()) {
            Resource entryResource = entry.getResource();
            String entryResourceId = entryResource.getId();
            boolean hasFullUrlDefined = !StringUtils.isBlank(entry.getFullUrl());

            if (entryResourceId.endsWith(resourceReferenceIdPart)) {
                return entryResource;
            } else if (hasFullUrlDefined) {
                if (entry.getFullUrl().endsWith(resourceReferenceIdPart)) {
                    return entryResource;
                }
            }
        }
        return null;
    }
}



