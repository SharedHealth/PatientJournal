package org.freeshr.journal.model;

import com.sun.syndication.feed.atom.Entry;
import org.freeshr.journal.infrastructure.AtomFeed;
import org.hl7.fhir.instance.model.*;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.freeshr.journal.model.EncounterBundlesData.fromFeedEntries;
import static org.freeshr.journal.utils.FileUtil.asString;
import static org.junit.Assert.assertEquals;

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
        assertEquals(11, observations.size());
    }

    @Test
    public void shouldGiveAllResourcesOfTypeCondition() throws Exception {
        List<Condition> conditions = encounterBundleData.getConditions();
        assertEquals(5, conditions.size());
    }

    @Test
    public void shouldGiveAllConditionOfTypeComplaint() throws Exception {
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
        List<FamilyHistory> familyHistories = encounterBundleData.getFamilyHistories();
        assertEquals(1, familyHistories.size());
        for (FamilyHistory familyHistory : familyHistories) {
            CodeableConcept relationship = familyHistory.getRelation().get(0).getRelationship();
        }
    }

    @Test
    public void shouldGiveAllResourcesOfTypeMedicationPrescription() throws Exception {
        List<MedicationPrescription> medicationPrescriptions = encounterBundleData.getMedicationPrescriptions();
        assertEquals(1, medicationPrescriptions.size());
    }

    @Test
    public void shouldGiveListOfAllTestsWithProperDetails() throws Exception {
        List<Entry> entries = new AtomFeed().parse(asString("encounters/encounterWithDiagnosticOrder.xml"));
        EncounterBundlesData encounterBundlesData = fromFeedEntries(entries);
        encounterBundleData = encounterBundlesData.getEncounterBundleDataList().get(0);

        List<TestOrder> testOrders = encounterBundleData.getTestOrders();
        assertEquals(3, testOrders.size());
        assertEquals("http://hrmtest.dghs.gov.bd/api/1.0/providers/113073.json", testOrders.get(0).getOrderer().getReferenceSimple());
        assertEquals("Bld", testOrders.get(0).getSample().getType().getCoding().get(0).getDisplaySimple());
    }
}
