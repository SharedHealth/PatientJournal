package org.freeshr.journal.infrastructure;

import com.sun.syndication.feed.atom.Category;
import com.sun.syndication.feed.atom.Entry;
import com.sun.syndication.feed.atom.Feed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.WireFeedInput;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class AtomFeed {
    public AtomFeed() {

    }

    public List<Entry> parse(String feedResponse) throws FeedException {
        WireFeedInput input = new WireFeedInput();
        Feed feed = (Feed) input.build(new StringReader(feedResponse));
        List<Entry> feedEntries = feed.getEntries();
        ArrayList<Entry> entries = new ArrayList<>();
        for (Entry entry : feedEntries) {
            if(!hasUpdatedEncounterInTheFeed(entry))
                entries.add(entry);
        }
        return entries;
    }

    private boolean hasUpdatedEncounterInTheFeed(Entry entry) {
        for (Object category : entry.getCategories()) {
            if(((Category)category).getTerm().contains("latest_update_event_id")) return true;
        }
        return false;
    }
}
