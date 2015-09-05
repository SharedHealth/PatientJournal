package org.freeshr.journal.tags;

import ca.uhn.fhir.model.dstu2.composite.*;
import ca.uhn.fhir.model.primitive.BooleanDt;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.StringDt;
import org.apache.log4j.Logger;
import org.freeshr.journal.utils.DateUtil;
import org.hl7.fhir.instance.model.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class TypeConverter {

    private static final String DATE_FORMAT = "dd MMM yyyy HH:mm";
    private static Logger logger = Logger.getLogger(TypeConverter.class);

    public static String convertToText(Object typeValue) {
        try {
            if (typeValue == null)
                return "";
            if (typeValue.getClass().equals(java.util.Date.class))
                return fromJavaUtilDate((java.util.Date) typeValue);
            if (typeValue.getClass().equals(Quantity.class))
                return fromQuantityDt((QuantityDt) typeValue);
            if (typeValue.getClass().equals(CodeableConceptDt.class))
                return fromCodeableConceptDt((CodeableConceptDt) typeValue);
            if (typeValue.getClass().equals(BoundCodeableConceptDt.class))
                return fromCodeableConceptDt((CodeableConceptDt) typeValue);
            if (typeValue.getClass().equals(Ratio.class))
                return fromRatio((Ratio) typeValue);
            if (typeValue.getClass().equals(PeriodDt.class))
                return fromPeriodDt((PeriodDt) typeValue);
            if (typeValue.getClass().equals(SampledData.class))
                return fromSampleData((SampledData) typeValue);
            if (typeValue.getClass().equals(StringDt.class))
                return fromStringDt((StringDt) typeValue);
            if (typeValue.getClass().equals(Attachment.class))
                return fromAttachment((Attachment) typeValue);
            if (typeValue.getClass().equals(Decimal.class))
                return fromDecimal((Decimal) typeValue);
            if (typeValue.getClass().equals(DateDt.class))
                return fromDateDt((DateDt) typeValue);
            if (typeValue.getClass().equals(AgeDt.class))
                return fromAgeDt((AgeDt) typeValue);
            if (typeValue.getClass().equals(DateTimeDt.class))
                return fromDateTimeDt((DateTimeDt) typeValue);
            if (typeValue.getClass().equals(BooleanDt.class))
                return fromBooleanDt((BooleanDt) typeValue);
            if (typeValue.getClass().equals(RangeDt.class))
                return fromRangeDt((RangeDt) typeValue);
            if (typeValue.getClass().equals(ResourceReference.class))
                return fromResourceReference((ResourceReference) typeValue);
            if (typeValue.getClass().equals(Schedule.class))
                return fromSchedule((Schedule) typeValue);
        } catch (Exception ex) {
            logger.error(String.format("Unable to parse type-value %s of type %s.", typeValue, typeValue.getClass().getCanonicalName()), ex);
        }
        return typeValue.toString();
    }

    private static String fromJavaUtilDate(java.util.Date typeValue) {
        return DateUtil.toDateString(typeValue, DATE_FORMAT);
    }

    private static String fromDateDt(DateDt typeValue) {
        java.util.Date value = typeValue.getValue();
        return fromJavaUtilDate(value);
    }

    private static String fromDecimal(Decimal typeValue) {
        return typeValue.getStringValue();
    }

    private static String fromAttachment(Attachment typeValue) {
        return typeValue.getUrlSimple();
    }

    private static String fromStringDt(StringDt typeValue) {
        return typeValue.getValue();
    }

    private static String fromSampleData(SampledData typeValue) {
        return typeValue.getDataSimple();
    }

    private static String fromPeriodDt(PeriodDt typeValue) {
        String start = (typeValue.getStart() != null) ? fromJavaUtilDate(typeValue.getStart()) : "Unknown";
        String end = (typeValue.getEnd() != null) ? fromJavaUtilDate(typeValue.getEnd()) : "Ongoing";
        return start + " - " + end;
    }

    private static String fromRatio(Ratio typeValue) {
        Quantity numerator = typeValue.getNumerator();
        Quantity denominator = typeValue.getDenominator();
        if (numerator != null && denominator != null)
            return numerator.getValueSimple() + "/" + denominator.getValueSimple();
        return "0";
    }

    private static String fromCodeableConceptDt(CodeableConceptDt typeValue) {
        if (typeValue.getCoding().isEmpty()) return "";
        return typeValue.getCoding().get(0).getDisplay();
    }

    private static String fromQuantityDt(QuantityDt typeValue) {
        BigDecimal value = typeValue.getValue();
        String units = typeValue.getUnits();

        if (value == null) return "";

        if (units != null)
            return value + " " + units;
        else
            return String.valueOf(value);
    }

    private static String fromDateTimeDt(DateTimeDt typeValue) {
        java.util.Date value = typeValue.getValue();
        return fromJavaUtilDate(value);
    }

    private static String fromAgeDt(AgeDt typeValue) {
        return typeValue.getValue() + " yrs";
    }

    private static String fromBooleanDt(BooleanDt typeValue) {
        return java.lang.Boolean.TRUE.equals(typeValue.getValue()) ? "yes" : "no";
    }

    private static String fromRangeDt(RangeDt typeValue) {
        QuantityDt low = typeValue.getLow();
        QuantityDt high = typeValue.getHigh();
        String lowerRange = (low != null) ? fromQuantityDt(low) : "";
        String higherRange = (high != null) ? fromQuantityDt(high) : "";
        return lowerRange + " - " + higherRange;
    }

    private static String fromDateAndTime(DateAndTime value) {
        return DateUtil.toDateString(DateUtil.parseDate(value.toString()), DATE_FORMAT);
    }

    private static String fromResourceReference(ResourceReference typeValue) {
        return typeValue.getDisplaySimple();
    }

    private static String fromSchedule(Schedule typeValue) {
        Schedule.ScheduleRepeatComponent repeat = typeValue.getRepeat();
        if (repeat == null) return "Improper Schedule";
        return String.format("%s times in a %s", repeat.getDurationSimple(), getUnitFullName(repeat.getUnitsSimple()));
    }

    private static String getUnitFullName(Schedule.UnitsOfTime unitsSimple) {
        List<String> unitKeys = Arrays.asList("s", "min", "h", "d", "wk", "mo", "a");
        List<String> unitValues = Arrays.asList("second.", "minute.", "hour.", "day.", "week.", "month.", "year.");

        int index = unitKeys.indexOf(unitsSimple.toCode());
        return unitValues.get(index);
    }


}
