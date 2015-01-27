package org.freeshr.journal.model;

import com.sun.syndication.feed.atom.Entry;
import org.freeshr.journal.infrastructure.AtomFeed;
import org.hl7.fhir.instance.model.Composition;
import org.hl7.fhir.instance.model.Condition;
import org.hl7.fhir.instance.model.Encounter;
import org.hl7.fhir.instance.model.Observation;
import org.junit.Test;

import java.util.List;

import static org.freeshr.journal.model.EncounterBundlesData.fromFeedEntries;
import static org.freeshr.journal.utils.FileUtil.asString;
import static org.junit.Assert.assertEquals;

public class EncounterBundleDataTest {

    @Test
    public void shouldGiveAllResourcesOfTypeComposition() throws Exception {
        List<Entry> entries = new AtomFeed().parse(asString("encounters/shrEncounterResponse.xml"));
        EncounterBundlesData encounterBundlesData = fromFeedEntries(entries);
        EncounterBundleData encounterBundleData = encounterBundlesData.getEncounterBundleDataList().get(0);
        List<Composition> compositions = encounterBundleData.getCompositions();
        assertEquals(1, compositions.size());
    }

    @Test
    public void shouldGiveAllResourcesOfTypeEncounter() throws Exception {
        List<Entry> entries = new AtomFeed().parse(asString("encounters/shrEncounterResponse.xml"));
        EncounterBundlesData encounterBundlesData = fromFeedEntries(entries);
        EncounterBundleData encounterBundleData = encounterBundlesData.getEncounterBundleDataList().get(0);
        List<Encounter> encounters = encounterBundleData.getEncounters();
        assertEquals(1, encounters.size());
    }

    @Test
    public void shouldGiveAllResourcesOfTypeObservation() throws Exception {
        List<Entry> entries = new AtomFeed().parse(asString("encounters/shrEncounterResponse.xml"));
        EncounterBundlesData encounterBundlesData = fromFeedEntries(entries);
        EncounterBundleData encounterBundleData = encounterBundlesData.getEncounterBundleDataList().get(0);
        List<Observation> observations = encounterBundleData.getObservations();
        assertEquals(0, observations.size());
    }

    @Test
    public void shouldGiveAllResourcesOfTypeCondition() throws Exception {
        List<Entry> entries = new AtomFeed().parse(asString("encounters/shrEncounterResponse.xml"));
        EncounterBundlesData encounterBundlesData = fromFeedEntries(entries);
        EncounterBundleData encounterBundleData = encounterBundlesData.getEncounterBundleDataList().get(0);
        List<Condition> conditions = encounterBundleData.getConditions();
        assertEquals(0, conditions.size());
    }
}