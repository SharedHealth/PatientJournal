package org.freeshr.journal.model;

import ca.uhn.fhir.model.dstu2.resource.*;
import com.sun.syndication.feed.atom.Entry;
import org.freeshr.journal.infrastructure.AtomFeed;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.freeshr.journal.model.EncounterBundlesData.fromFeedEntries;
import static org.freeshr.journal.utils.FileUtil.asString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class EncounterBundleDataTest {

    private EncounterBundleData encounterBundleData;

    @Before
    public void setUp() throws Exception {
        List<Entry> entries = new AtomFeed().parse(asString("encounters/encounterWithAllResources.xml"));
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
    public void shouldGiveAllResourcesOfTypeObservation() throws Exception {
        List<Observation> observations = encounterBundleData.getObservations();
        assertEquals(16, observations.size());
    }

    @Test
    public void shouldGiveAllResourcesOfTypeCondition() throws Exception {
        List<Condition> conditions = encounterBundleData.getConditions();
        assertEquals(4, conditions.size());
    }

    @Test
    public void shouldGiveAllConditionOfTypeCompliant() throws Exception {
        List<Condition> complaintConditions = encounterBundleData.getComplaintConditions();
        assertEquals(1, complaintConditions.size());
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
    public void shouldGiveAllResourcesOfTypeMedicationPrescription() throws Exception {
        List<MedicationPrescription> medicationPrescriptions = encounterBundleData.getMedicationPrescriptions();
        assertEquals(0, medicationPrescriptions.size());
    }

    @Test
    public void shouldGiveListOfAllTestsWithProperDetails() throws Exception {
        List<TestOrder> testOrders = encounterBundleData.getTestOrders();
        assertEquals(2, testOrders.size());
        assertEquals("http://172.18.46.199:8080/api/1.0/providers/18.json", testOrders.get(0).getOrderer().getReference().getValueAsString());
        assertEquals("Bld", testOrders.get(0).getSample().getType().getCoding().get(0).getDisplay());
    }

    @Test
    public void shouldGetAllResourcesOfTypeProcedure() throws Exception {
        List<Procedure> procedures = encounterBundleData.getProcedures();
        assertEquals(3, procedures.size());
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

        SHRObservation bloodPressureObservation = thirdObservation.getChildren().get(0);
        assertSHRObservation(bloodPressureObservation, 1, 2);
    }

    private void assertSHRObservation(SHRObservation firstObservation, int depth, int numberOfChildren) {
        assertNotNull(firstObservation);
        assertEquals(depth, firstObservation.getDepth());
        assertEquals(numberOfChildren, firstObservation.getChildren().size());
    }
}
