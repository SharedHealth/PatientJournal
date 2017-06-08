package org.freeshr.journal.model;

import com.sun.syndication.feed.atom.Content;
import com.sun.syndication.feed.atom.Entry;
import org.freeshr.journal.infrastructure.FhirBundleUtil;
import org.hl7.fhir.dstu3.model.Bundle;

import java.util.ArrayList;
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
            Bundle bundle = FhirBundleUtil.parseBundle(entryContent, "xml");
            encounterBundle.setBundle(bundle);
            for (Bundle.BundleEntryComponent bundleEntry : bundle.getEntry()) {
                encounterBundle.addResource(bundleEntry.getResource());
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
}
