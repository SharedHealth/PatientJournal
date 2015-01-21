package org.freeshr.journal.tags.fhir;

import org.freeshr.journal.tags.AttributeProcessor;
import org.hl7.fhir.instance.model.Age;
import org.hl7.fhir.instance.model.DateTime;
import org.hl7.fhir.instance.model.Type;

public class OnSetAttrProcessor extends AttributeProcessor<Type> {

    public OnSetAttrProcessor() {
        super("onset");
    }

    @Override
    protected String process(Type onset) {
        if (onset == null)
            return "";
        if (onset instanceof Age)
            return getOnSetAgeSimple((Age) onset);
        if (onset instanceof DateTime)
            return getOnSetDateSimple((DateTime) onset);

        return "Unknown";
    }

    private String getOnSetDateSimple(DateTime onset) {
        return onset.getValue().toHumanDisplay();
    }

    private String getOnSetAgeSimple(Age onset) {
        return onset.getValueSimple() + " " + onset.getUnitsSimple();
    }
}


