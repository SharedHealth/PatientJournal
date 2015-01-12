package org.freeshr.journal.infrastructure;

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
        List feedEntries = feed.getEntries();
        ArrayList<Entry> entries = new ArrayList<>();
        for (Object entry : feedEntries) {
            entries.add((Entry) entry);
        }
        return entries;
    }
}
