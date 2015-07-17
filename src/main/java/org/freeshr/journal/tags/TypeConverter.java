package org.freeshr.journal.tags;

import org.hl7.fhir.instance.model.*;
import org.hl7.fhir.instance.model.Boolean;

public class TypeConverter {

    public static String convertToText(Type typeValue) {
        if (typeValue == null)
            return "";
        if (typeValue.getClass().equals(Quantity.class))
            return fromQuantity((Quantity) typeValue);
        if (typeValue.getClass().equals(CodeableConcept.class))
            return fromCodeableConcept((CodeableConcept) typeValue);
        if (typeValue.getClass().equals(Ratio.class))
            return fromRatio((Ratio) typeValue);
        if (typeValue.getClass().equals(Period.class))
            return fromPeriod((Period) typeValue);
        if (typeValue.getClass().equals(SampledData.class))
            return fromSampleData((SampledData) typeValue);
        if (typeValue.getClass().equals(String_.class))
            return fromString_((String_) typeValue);
        if (typeValue.getClass().equals(Attachment.class))
            return fromAttachment((Attachment) typeValue);
        if (typeValue.getClass().equals(Decimal.class))
            return fromDecimal((Decimal) typeValue);
        if (typeValue.getClass().equals(Date.class))
            return fromDate((Date) typeValue);
        if (typeValue.getClass().equals(Age.class))
            return fromAge((Age) typeValue);
        if (typeValue.getClass().equals(DateTime.class))
            return fromDateTime((DateTime) typeValue);
        if (typeValue.getClass().equals(Boolean.class))
            return fromBoolean((Boolean) typeValue);
        if (typeValue.getClass().equals(Range.class))
            return fromRange((Range) typeValue);

        return "Unknown";
    }

    private static String fromDate(Date typeValue) {
        return typeValue.getValue().toHumanDisplay();
    }

    private static String fromDecimal(Decimal typeValue) {
        return typeValue.getStringValue();
    }

    private static String fromAttachment(Attachment typeValue) {
        return typeValue.getUrlSimple();
    }

    private static String fromString_(String_ typeValue) {
        return typeValue.getValue();
    }

    private static String fromSampleData(SampledData typeValue) {
        return typeValue.getDataSimple();
    }

    private static String fromPeriod(Period typeValue) {
        return typeValue.getStartSimple().toHumanDisplay() + " to " + typeValue.getEndSimple().toHumanDisplay();
    }

    private static String fromRatio(Ratio typeValue) {
        return typeValue.getNumerator().getValueSimple() + "/" + typeValue.getDenominator().getValueSimple();
    }

    private static String fromCodeableConcept(CodeableConcept typeValue) {
        if (typeValue.getCoding().isEmpty()) return "";
        return typeValue.getCoding().get(0).getDisplaySimple();
    }

    private static String fromQuantity(Quantity typeValue) {
        return typeValue.getValueSimple() + " " + typeValue.getUnitsSimple();
    }

    private static String fromDateTime(DateTime typeValue) {
        return typeValue.getValue().toHumanDisplay();
    }

    private static String fromAge(Age typeValue) {
        return typeValue.getValueSimple() + " " + typeValue.getUnitsSimple();
    }

    private static String fromBoolean(Boolean typeValue) {
        return typeValue.getStringValue();
    }

    private static String fromRange(Range typeValue) {
        return typeValue.getLow().getValueSimple() + "-" + typeValue.getHigh().getValueSimple();
    }
}
