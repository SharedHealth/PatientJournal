package org.freeshr.journal.tags;

import ca.uhn.fhir.model.dstu2.composite.*;
import ca.uhn.fhir.model.primitive.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.freeshr.journal.utils.DateUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TypeConverter {

    private static final String DATE_FORMAT = "dd MMM yyyy HH:mm";

    public static final String INVALID_PERIOD = "Invalid Period";
    public static final String PERIOD_START_UNKNOWN = "Unknown";
    public static final String INVALID_REFERENCE = "invalid reference";


    private static Logger logger = Logger.getLogger(TypeConverter.class);

    public static String convertToText(Object typeValue) {
        try {
            if (typeValue == null)
                return "";

            if (typeValue instanceof List<?>)
                return fromList((List) typeValue);

            if (typeValue.getClass().equals(Date.class))
                return fromJavaUtilDate((Date) typeValue);

            if (typeValue.getClass().equals(QuantityDt.class))
                return fromQuantityDt((QuantityDt) typeValue);

            if (typeValue.getClass().equals(CodeableConceptDt.class))
                return fromCodeableConceptDt((CodeableConceptDt) typeValue);

            if (typeValue.getClass().equals(BoundCodeableConceptDt.class))
                return fromCodeableConceptDt((CodeableConceptDt) typeValue);

            if (typeValue.getClass().equals(RatioDt.class))
                return fromRatioDt((RatioDt) typeValue);

            if (typeValue.getClass().equals(PeriodDt.class))
                return fromPeriodDt((PeriodDt) typeValue);

            if (typeValue.getClass().equals(SampledDataDt.class))
                return fromSampleDataDt((SampledDataDt) typeValue);

            if (typeValue.getClass().equals(StringDt.class))
                return fromStringDt((StringDt) typeValue);

            if (typeValue.getClass().equals(AttachmentDt.class))
                return fromAttachmentDt((AttachmentDt) typeValue);

            if (typeValue.getClass().equals(DateDt.class))
                return fromDateDt((DateDt) typeValue);

            if (typeValue.getClass().equals(AgeDt.class))
                return fromAgeDt((AgeDt) typeValue);

            if (typeValue.getClass().equals(DateTimeDt.class))
                return fromDateTimeDt((DateTimeDt) typeValue);

            if (typeValue.getClass().equals(Boolean.class))
                return fromBoolean((Boolean) typeValue);

            if (typeValue.getClass().equals(BooleanDt.class))
                return fromBooleanDt((BooleanDt) typeValue);

            if (typeValue.getClass().equals(RangeDt.class))
                return fromRangeDt((RangeDt) typeValue);

            if (typeValue.getClass().equals(DecimalDt.class))
                return fromDecimalDt((DecimalDt) typeValue);

            if (typeValue.getClass().equals(ResourceReferenceDt.class))
                return fromResourceReferenceDt((ResourceReferenceDt) typeValue);

        } catch (Exception ex) {
            logger.error(String.format("Unable to parse type-value %s of type %s.", typeValue, typeValue.getClass().getCanonicalName()), ex);
        }
        return typeValue.toString();
    }

    private static String fromBoolean(Boolean typeValue) {
        return Boolean.TRUE.equals(typeValue) ? "YES" : "NO";
    }

    private static String fromList(List values) {
        ArrayList<String> valuesInText = new ArrayList<>();
        for (Object value : values) {
            valuesInText.add(convertToText(value));
        }
        return StringUtils.join(valuesInText, ",");
    }

    private static String fromJavaUtilDate(Date typeValue) {
        return DateUtil.toDateString(typeValue, DATE_FORMAT);
    }

    private static String fromDateDt(DateDt typeValue) {
        Date value = typeValue.getValue();
        return fromJavaUtilDate(value);
    }

    private static String fromDecimalDt(DecimalDt typeValue) {
        return typeValue.getValueAsString();
    }

    private static String fromAttachmentDt(AttachmentDt typeValue) {
        return typeValue.getUrl();
    }

    private static String fromStringDt(StringDt typeValue) {
        return typeValue.getValue();
    }

    private static String fromSampleDataDt(SampledDataDt typeValue) {
        return typeValue.getData();
    }

    private static String fromPeriodDt(PeriodDt typeValue) {
        if (typeValue.getStart() == null && typeValue.getEnd() == null) return INVALID_PERIOD;
        String start = (typeValue.getStart() != null) ? fromJavaUtilDate(typeValue.getStart()) : PERIOD_START_UNKNOWN;
        String end;
        if (typeValue.getEnd() == null)
            return start;
        else
            end = fromJavaUtilDate(typeValue.getEnd());

        return start + " - " + end;
    }

    private static String fromRatioDt(RatioDt typeValue) {
        QuantityDt numerator = typeValue.getNumerator();
        QuantityDt denominator = typeValue.getDenominator();
        if (numerator == null || numerator.getValue() == null) return "0";
        if (denominator == null || denominator.getValue() == null) return "0";
        return numerator.getValue() + "/" + denominator.getValue();
    }

    private static String fromCodeableConceptDt(CodeableConceptDt typeValue) {
        if (!typeValue.getCoding().isEmpty())
            return typeValue.getCoding().get(0).getDisplay();
        return typeValue.getText() != null ? typeValue.getText() : "";
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
        Date value = typeValue.getValue();
        return fromJavaUtilDate(value);
    }

    private static String fromAgeDt(AgeDt typeValue) {
        return typeValue.getValue() + " yrs";
    }

    private static String fromBooleanDt(BooleanDt typeValue) {
        return Boolean.TRUE.equals(typeValue.getValue()) ? "YES" : "NO";
    }

    private static String fromRangeDt(RangeDt typeValue) {
        QuantityDt low = typeValue.getLow();
        QuantityDt high = typeValue.getHigh();
        String lowerRange = (low != null) ? fromQuantityDt(low) : "";
        String higherRange = (high != null) ? fromQuantityDt(high) : "";
        return lowerRange + " - " + higherRange;
    }

    private static String fromResourceReferenceDt(ResourceReferenceDt typeValue) {
        IdDt reference = typeValue.getReference();
        return reference == null ? INVALID_REFERENCE : reference.getValue();
    }


}
