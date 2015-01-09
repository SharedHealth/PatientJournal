package infrastructure;

import com.sun.syndication.feed.atom.Entry;
import org.junit.Test;

import java.util.List;

import static org.freeshr.journal.utils.FileUtil.asString;
import static org.junit.Assert.*;

public class AtomFeedTest {
    @Test
    public void shouldReadEncounterFeedForPatient() throws Exception {
        List<Entry> feed = new AtomFeed().parse(asString("encounters/shrEncounterResponse.xml"));
        assertEquals(1, feed.size());
    }
}