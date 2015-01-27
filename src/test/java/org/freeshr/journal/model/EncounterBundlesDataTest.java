package org.freeshr.journal.model;

import com.sun.syndication.feed.atom.Entry;
import org.freeshr.journal.infrastructure.AtomFeed;
import org.hl7.fhir.instance.model.Encounter;
import org.hl7.fhir.instance.model.ResourceType;
import org.junit.Test;

import java.util.List;

import static org.freeshr.journal.model.EncounterBundlesData.fromFeedEntries;
import static org.freeshr.journal.utils.FileUtil.asString;
import static org.junit.Assert.assertEquals;

public class EncounterBundlesDataTest {

    @Test
    public void shouldDeserializeEncounters() throws Exception {
        List<Entry> entries = new AtomFeed().parse(asString("encounters/shrEncounterResponse.xml"));
        EncounterBundlesData encounterBundlesData = fromFeedEntries(entries);
        assertEquals(1, encounterBundlesData.getEncounterBundleDataList().size());
        assertEquals(2, encounterBundlesData.getEncounterBundleDataList().get(0).getEncounterBundle().getResources().size());
    }

    @Test
    public void shouldGetEncounter() throws Exception {
        List<Entry> entries = new AtomFeed().parse(asString("encounters/shrEncounterResponse.xml"));
        EncounterBundlesData encounterBundlesData = fromFeedEntries(entries);
        EncounterBundle encounterBundle = encounterBundlesData.getEncounterBundleDataList().get(0).getEncounterBundle();
        Encounter encounter = encounterBundle.getEncounter();
        assertEquals(ResourceType.Encounter, encounter.getResourceType());
    }
}