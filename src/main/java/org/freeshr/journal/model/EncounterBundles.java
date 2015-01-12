package org.freeshr.journal.model;

import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import org.hl7.fhir.instance.formats.ParserBase;
import org.hl7.fhir.instance.formats.XmlParser;
import org.hl7.fhir.instance.model.AtomEntry;
import org.hl7.fhir.instance.model.AtomFeed;
import org.hl7.fhir.instance.model.Resource;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class EncounterBundles {
    List<EncounterBundle> encounterBundles = new ArrayList<>();

    public List<EncounterBundle> getEncounterBundles() {
        return encounterBundles;
    }

    public void addEncounterBundle(EncounterBundle encounterBundle){
        encounterBundles.add(encounterBundle);
    }

    public static EncounterBundles fromFeedEntries(List<Entry> entries) {
        EncounterBundles encounterBundles = new EncounterBundles();
        for (Entry entry : entries) {
            EncounterBundle encounterBundle = new EncounterBundle();
            String entryContent = getEntryContent(entry);
            AtomFeed encounterFeed = getResourceOrFeed(entryContent).getFeed();
            for (AtomEntry<? extends Resource> atomEntry : encounterFeed.getEntryList()) {
                encounterBundle.addResource(atomEntry.getResource());
            }
            encounterBundles.addEncounterBundle(encounterBundle);
        }
        return encounterBundles;
    }

    private static String getEntryContent(Entry entry) {
        if (entry.getContents().isEmpty()) {
            return null;
        }
        String value = ((Content) (entry.getContents().get(0))).getValue();
        value = value.trim();
        return value.replaceFirst("^<!\\[CDATA\\[", "").replaceFirst("\\]\\]>$", "");
    }

    private static ParserBase.ResourceOrFeed getResourceOrFeed(String entryContent) {
        ParserBase.ResourceOrFeed resource;
        try {
            resource = new XmlParser(true).parseGeneral(new ByteArrayInputStream(entryContent.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse XML", e);
        }
        return resource;
    }
}
