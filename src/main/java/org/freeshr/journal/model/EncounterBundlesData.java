package org.freeshr.journal.model;

import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import org.hl7.fhir.instance.formats.ParserBase;
import org.hl7.fhir.instance.formats.XmlComposer;
import org.hl7.fhir.instance.formats.XmlParser;
import org.hl7.fhir.instance.model.AtomEntry;
import org.hl7.fhir.instance.model.AtomFeed;
import org.hl7.fhir.instance.model.Resource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EncounterBundlesData {
    List<EncounterBundleData> encounterBundleDataList = new ArrayList<>();

    public List<EncounterBundleData> getEncounterBundleDataList() {
        return encounterBundleDataList;
    }

    public void addEncounterBundleModel(EncounterBundleData encounterBundleData) {
        encounterBundleDataList.add(encounterBundleData);
    }

    public static EncounterBundlesData fromFeedEntries(List<Entry> entries) {
        EncounterBundlesData encounterBundlesData = new EncounterBundlesData();
        for (Entry entry : entries) {
            EncounterBundle encounterBundle = new EncounterBundle();
            String entryContent = getEntryContent(entry);
            AtomFeed encounterFeed = getResourceOrFeed(entryContent).getFeed();
            encounterBundle.addFeed(encounterFeed);
            for (AtomEntry<? extends Resource> atomEntry : encounterFeed.getEntryList()) {
                encounterBundle.addResource(atomEntry.getResource());
            }
            encounterBundlesData.addEncounterBundleModel(new EncounterBundleData(encounterBundle));
        }
        return encounterBundlesData;
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

    @Override
    public String toString() {
        StringBuilder response = new StringBuilder();
        for (EncounterBundleData encounterBundleData : encounterBundleDataList) {
            EncounterBundle encounterBundle = encounterBundleData.getEncounterBundle();
            for (Resource resource : encounterBundle.getResources()) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                try {
                    new XmlComposer().compose(byteArrayOutputStream, resource, true);
                    response.append(new String(byteArrayOutputStream.toByteArray()));
                } catch (Exception e) {
                    e.printStackTrace();
                    response.append(Arrays.toString(e.getStackTrace()));
                }
            }
        }

        return response.toString();
    }
}
