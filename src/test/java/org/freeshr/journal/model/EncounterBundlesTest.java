package org.freeshr.journal.model;

import com.sun.syndication.feed.atom.Entry;
import org.freeshr.journal.infrastructure.AtomFeed;
import org.junit.Test;

import java.util.List;

import static org.freeshr.journal.model.EncounterBundles.fromFeedEntries;
import static org.freeshr.journal.utils.FileUtil.asString;
import static org.junit.Assert.assertEquals;

public class EncounterBundlesTest {

    @Test
    public void shouldDeserializeEncounters() throws Exception {
        List<Entry> entries = new AtomFeed().parse(asString("encounters/shrEncounterResponse.xml"));
        EncounterBundles encounterBundles = fromFeedEntries(entries);
        assertEquals(1, encounterBundles.getEncounterBundles().size());
        assertEquals(2, encounterBundles.getEncounterBundles().get(0).getResources().size());
    }
}