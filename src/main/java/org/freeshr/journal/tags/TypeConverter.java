package org.freeshr.journal.tags;

import org.apache.log4j.Logger;
import org.freeshr.journal.utils.DateUtil;
import org.hl7.fhir.instance.model.*;
import org.hl7.fhir.instance.model.Boolean;

public class TypeConverter {

    private static Logger logger = Logger.getLogger(TypeConverter.class);

    public static String convertToText(Type typeValue) {
        try {
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
        } catch (Exception ex) {
            logger.error(String.format("Unable to parse type-value %s of type %s.", typeValue, typeValue.getClass().getCanonicalName()), ex);
        }
        return "Unknown";
    }

    private static String fromDate(Date typeValue) {
        DateAndTime value = typeValue.getValue();
        return fromDateAndTime(value);
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
        String start = (typeValue.getStartSimple() != null) ? fromDateAndTime(typeValue.getStartSimple()) : "Unknown";
        String end = (typeValue.getEndSimple() != null) ? fromDateAndTime(typeValue.getEndSimple()) : "Ongoing";
        return start + " to " + end;
    }

    private static String fromRatio(Ratio typeValue) {
        Quantity numerator = typeValue.getNumerator();
        Quantity denominator = typeValue.getDenominator();
        if (numerator != null && denominator != null)
            return numerator.getValueSimple() + "/" + denominator.getValueSimple();
        return "0";
    }

    private static String fromCodeableConcept(CodeableConcept typeValue) {
        if (typeValue.getCoding().isEmpty()) return "";
        return typeValue.getCoding().get(0).getDisplaySimple();
    }

    private static String fromQuantity(Quantity typeValue) {
        return typeValue.getValueSimple() + " " + typeValue.getUnitsSimple();
    }

    private static String fromDateTime(DateTime typeValue) {
        DateAndTime value = typeValue.getValue();
        return fromDateAndTime(value);
    }

    private static String fromAge(Age typeValue) {
        return typeValue.getValueSimple() + " yrs";
    }

    private static String fromBoolean(Boolean typeValue) {
        return typeValue.getStringValue();
    }

    private static String fromRange(Range typeValue) {
        Quantity low = typeValue.getLow();
        Quantity high = typeValue.getHigh();
        String lowerRange = (low != null) ? low.getValueSimple() + " " + low.getUnitsSimple() : "";
        String higherRange = (high != null) ? high.getValueSimple() + " " + high.getUnitsSimple() : "";
        return lowerRange + " - " + higherRange;
    }

    private static String fromDateAndTime(DateAndTime value) {
        return DateUtil.toDateString(DateUtil.parseDate(value.toString()), "dd MMM yyyy HH:mm");
    }
}
