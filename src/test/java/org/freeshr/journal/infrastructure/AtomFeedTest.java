package org.freeshr.journal.infrastructure;

import com.sun.syndication.feed.atom.Entry;
import org.junit.Test;

import java.util.List;

import static org.freeshr.journal.utils.FileUtil.asString;
import static org.junit.Assert.*;

public class AtomFeedTest {
    @Test
    public void shouldReadEncounterFeedForPatient() throws Exception {
        List<Entry> entries = new AtomFeed().parse(asString("encounters/dstu2/shrEncounterResponse.xml"));
        assertEquals(3, entries.size());
    }

    @Test
    public void shouldSkipEncounterEntryIfItHasTheUpdatedEncounterInTheFeed() throws Exception {
        List<Entry> entries = new AtomFeed().parse(asString("encounters/dstu2/patientUpdatedEncounterFeed.xml"));
        assertEquals(4,entries.size());
    }
}
