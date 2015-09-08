package org.freeshr.journal.model;

import com.sun.syndication.feed.atom.Entry;
import org.freeshr.journal.infrastructure.AtomFeed;
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
        assertEquals(3, encounterBundlesData.getEncounterBundleDataList().size());
        assertEquals(2, encounterBundlesData.getEncounterBundleDataList().get(0).getEncounterBundle().getResources().size());
        assertEquals(4, encounterBundlesData.getEncounterBundleDataList().get(1).getEncounterBundle().getResources().size());
        assertEquals(10, encounterBundlesData.getEncounterBundleDataList().get(2).getEncounterBundle().getResources().size());
    }
}