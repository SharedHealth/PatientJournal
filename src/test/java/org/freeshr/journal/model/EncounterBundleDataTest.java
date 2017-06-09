package org.freeshr.journal.model;

import com.sun.syndication.feed.atom.Entry;
import org.freeshr.journal.infrastructure.AtomFeed;
import org.freeshr.journal.utils.DateUtil;
import org.hl7.fhir.dstu3.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.freeshr.journal.model.EncounterBundlesData.fromFeedEntries;
import static org.freeshr.journal.tags.TypeConverter.convertToText;
import static org.freeshr.journal.utils.FileUtil.asString;
import static org.junit.Assert.*;

public class EncounterBundleDataTest {

    private EncounterBundleData encounterBundleData;

    @Before
    public void setUp() throws Exception {
        List<Entry> entries = new AtomFeed().parse(asString("encounters/stu3/encounterWithAllResources.xml"));
        EncounterBundlesData encounterBundlesData = fromFeedEntries(entries);
        encounterBundleData = encounterBundlesData.getEncounterBundleDataList().get(0);
    }

    @Test
    public void shouldGiveAllResourcesOfTypeComposition() throws Exception {
        List<Composition> compositions = encounterBundleData.getCompositions();
        assertEquals(1, compositions.size());
    }

    @Test
    public void shouldGiveAllResourcesOfTypeEncounter() throws Exception {
        List<Encounter> encounters = encounterBundleData.getEncounters();
        assertEquals(1, encounters.size());
    }

    @Test
    public void shouldGiveAllResourcesOfTypeCondition() throws Exception {
        List<Condition> conditions = encounterBundleData.getConditions();
        assertEquals(5, conditions.size());
    }

    @Test
    public void shouldGiveAllConditionOfTypeCompliant() throws Exception {
        List<Condition> complaintConditions = encounterBundleData.getComplaintConditions();
        assertEquals(2, complaintConditions.size());
    }

    @Test
    public void shouldGiveAllConditionOfTypeDiagnosis() throws Exception {
        List<Condition> complaintConditions = encounterBundleData.getDiagnosisConditions();
        assertEquals(1, complaintConditions.size());
    }

    @Test
    public void shouldGiveAllConditionOfTypeFinding() throws Exception {
        List<Condition> complaintConditions = encounterBundleData.getFindingConditions();
        assertEquals(1, complaintConditions.size());
    }

    @Test
    public void shouldGiveAllConditionOfTypeSymptom() throws Exception {
        List<Condition> complaintConditions = encounterBundleData.getSymptomConditions();
        assertEquals(1, complaintConditions.size());
    }

    @Test
    public void shouldGiveAllResourcesOfTypeFamilyHistory() throws Exception {
        List<FamilyMemberHistory> familyHistories = encounterBundleData.getFamilyMemberHistories();
        assertEquals(3, familyHistories.size());
    }

    @Test
    public void shouldGiveAllResourcesOfTypeMedicationOrder() throws Exception {
        List<MedicationRequest> medicationRequests = encounterBundleData.getMedicationRequests();
        assertEquals(1, medicationRequests.size());
    }

    @Test
    public void shouldGetAllTestResults() throws Exception {
        List<TestResult> testResults = encounterBundleData.getTestResults();
        assertEquals(4, testResults.size());

        assertNotNull(testResults.get(0).getName());
        assertNotNull(testResults.get(1).getName());

        List<SHRObservation> firstTestResults = testResults.get(0).getResults();
        assertEquals(1, firstTestResults.size());
        List<SHRObservation> secondTestResults = testResults.get(1).getResults();
        assertEquals(2, secondTestResults.size());
        assertEquals(3, secondTestResults.get(1).getChildren().size());
    }

    @Test
    public void shouldGetAllOrderFulfillments() throws Exception {
        List<Entry> entries = new AtomFeed().parse(asString("encounters/stu3/encounterWithRadiologyFulfillment.xml"));
        EncounterBundlesData encounterBundlesData = fromFeedEntries(entries);
        encounterBundleData = encounterBundlesData.getEncounterBundleDataList().get(0);

        List<TestResult> testResults = encounterBundleData.getTestResults();
        assertEquals(1, testResults.size());

        TestResult testResult = testResults.get(0);
        assertNotNull(testResult.getName());
        assertNotNull(testResult.getType());
        assertEquals("http://172.18.46.199:8084/api/1.0/providers/20.json", testResult.getPerformer().getReference());
        assertEquals("Radiology", convertToText(testResult.getType()));
        assertEquals(DateUtil.parseDate("2016-04-04T17:32:34.000+05:30"), testResult.getDate());
        List<SHRObservation> testObservations = testResult.getResults();
        assertEquals(3, testObservations.size());
    }

    @Test
    public void shouldGetAllOrderFulfillmentAndDefaultTypeAsLaboratory() throws Exception {
        List<Entry> entries = new AtomFeed().parse(asString("encounters/stu3/encounterWithFulfillmentWithoutCategory.xml"));
        EncounterBundlesData encounterBundlesData = fromFeedEntries(entries);
        encounterBundleData = encounterBundlesData.getEncounterBundleDataList().get(0);

        List<TestResult> testResults = encounterBundleData.getTestResults();
        assertEquals(1, testResults.size());

        TestResult testResult = testResults.get(0);
        assertNotNull(testResult.getName());
        assertNotNull(testResult.getType());

        assertEquals("Laboratory", convertToText(testResult.getType()));
        List<SHRObservation> testObservations = testResult.getResults();
        assertEquals(3, testObservations.size());
    }

    @Test
    public void shouldGiveAllResourcesOfTypeProcedureOrder() throws Exception {
        List<Entry> entries = new AtomFeed().parse(asString("encounters/stu3/encounterWithProcedureRequest.xml"));
        EncounterBundlesData encounterBundlesData = fromFeedEntries(entries);
        encounterBundleData = encounterBundlesData.getEncounterBundleDataList().get(0);

        List<Order> orders = encounterBundleData.getOrders();
        assertEquals(2, orders.size());
        assertEquals("active", orders.get(0).getStatus());
        assertEquals("note one", orders.get(0).getNotes());
        assertEquals(DateUtil.parseDate("2016-02-22T11:47:25.000+05:30"), orders.get(0).getDate());
        assertEquals("http://172.18.46.199:8084/api/1.0/providers/24.json", orders.get(0).getOrderer().getReference());
        assertEquals("http://172.18.46.199:8084/api/1.0/facilities/10019842.json", orders.get(0).getFacility().getReference());
    }

    @Test
    public void shouldGiveAllResourcesOfTypeProcedureRequestAndCategoryRAD() throws Exception {
        List<Entry> entries = new AtomFeed().parse(asString("encounters/stu3/encounterWithRadiologyOrder.xml"));
        EncounterBundlesData encounterBundlesData = fromFeedEntries(entries);
        encounterBundleData = encounterBundlesData.getEncounterBundleDataList().get(0);

        List<Order> order = encounterBundleData.getOrders();
        assertEquals(1, order.size());
        Order radiologyOrder = order.get(0);
        assertEquals("Plain Radiography of Skull", convertToText(radiologyOrder.getCode()));
        assertEquals("http://172.18.46.199:8084/api/1.0/providers/20.json", convertToText(radiologyOrder.getOrderer()));
        assertEquals("RAD", radiologyOrder.getType());
        assertEquals("active", radiologyOrder.getStatus());
        assertEquals(DateUtil.parseDate("2016-04-04T10:41:15.000+05:30"), radiologyOrder.getDate());
    }

    @Test
    public void shouldGiveAllResourcesOfTypeDiagnosticOrder() throws Exception {
        List<Entry> entries = new AtomFeed().parse(asString("encounters/stu3/encounterWithOrderWithoutExtention.xml"));
        EncounterBundlesData encounterBundlesData = fromFeedEntries(entries);
        encounterBundleData = encounterBundlesData.getEncounterBundleDataList().get(0);

        List<Order> testOrders = encounterBundleData.getOrders();
        assertEquals(1, testOrders.size());
        Order testOrder = testOrders.get(0);
        assertEquals("Plain Radiography of Skull", convertToText(testOrder.getCode()));
        assertEquals("http://172.18.46.199:8084/api/1.0/providers/20.json", convertToText(testOrder.getOrderer()));
        assertEquals("LAB", convertToText(testOrder.getType()));
    }

    @Test
    public void shouldNotGiveResourcesOfTypeDiagnosticOrderIfStatusIsCancelled() throws Exception {
        List<Entry> entries = new AtomFeed().parse(asString("encounters/stu3/encounterWithCancelledOrder.xml"));
        EncounterBundlesData encounterBundlesData = fromFeedEntries(entries);
        encounterBundleData = encounterBundlesData.getEncounterBundleDataList().get(0);

        List<Order> testOrders = encounterBundleData.getOrders();
        assertEquals(1, testOrders.size());
        Order testOrder = testOrders.get(0);
        assertEquals("Plain Radiography of Skull", convertToText(testOrder.getCode()));
        assertEquals("http://172.18.46.199:8084/api/1.0/providers/20.json", convertToText(testOrder.getOrderer()));
        assertEquals("RAD", convertToText(testOrder.getType()));
        assertEquals("cancelled", testOrder.getStatus());
    }

    @Test
    public void shouldGetAllSHRProcedures() throws Exception {
        List<SHRProcedure> shrProcedures = encounterBundleData.getSHRProcedures();

        assertEquals(1, shrProcedures.size());
        SHRProcedure firstProcedure = shrProcedures.get(0);

        assertNotNull(firstProcedure.getDate());
        assertNotNull(firstProcedure.getType());
        assertNotNull(firstProcedure.getOutcome());
        assertEquals(2, firstProcedure.getFollowUp().size());
        assertEquals(1, firstProcedure.getProcedureReports().size());
    }

    @Test
    public void shouldGetAllShrObservationsAccordingToHierarchy() throws Exception {
        List<SHRObservation> shrObservations = encounterBundleData.getSHRObservations();
        assertEquals(3, shrObservations.size());
        SHRObservation firstObservation = shrObservations.get(0);
        SHRObservation secondObservation = shrObservations.get(1);
        SHRObservation thirdObservation = shrObservations.get(2);

        assertSHRObservation(firstObservation, 0, 4);
        assertSHRObservation(secondObservation, 0, 4);
        assertSHRObservation(thirdObservation, 0, 3);

        SHRObservation vitals = thirdObservation.getChildren().get(0);
        assertSHRObservation(vitals, 1, 2);
    }

    private void assertSHRObservation(SHRObservation observation, int depth, int numberOfChildren) {
        assertNotNull(observation);
        assertEquals(depth, observation.getDepth());
        assertEquals(numberOfChildren, observation.getChildren().size());
    }
}
