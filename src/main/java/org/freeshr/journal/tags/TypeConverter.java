package org.freeshr.journal.tags;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.freeshr.journal.utils.DateUtil;
import org.hl7.fhir.dstu3.model.*;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class TypeConverter {
    private static final String CUSTOM_DOSAGE_EXTENSION_URL = "https://sharedhealth.atlassian.net/wiki/display/docs/fhir-extensions#DosageInstructionCustomDosage";
    public static String AFTERNOON_DOSE = "afternoonDose";
    public static String MORNING_DOSE = "morningDose";
    public static String EVENING_DOSE = "eveningDose";

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

            if (typeValue.getClass().equals(Quantity.class))
                return fromQuantity((Quantity) typeValue);

            if (typeValue.getClass().equals(SimpleQuantity.class))
                return fromQuantity((Quantity) typeValue);

            if (typeValue.getClass().equals(CodeableConcept.class))
                return fromCodeableConcept((CodeableConcept) typeValue);

            if (typeValue.getClass().equals(Ratio.class))
                return fromRatio((Ratio) typeValue);

            if (typeValue.getClass().equals(Period.class))
                return fromPeriod((Period) typeValue);

            if (typeValue.getClass().equals(SampledData.class))
                return fromSampleData((SampledData) typeValue);

            if (typeValue.getClass().equals(StringType.class))
                return fromStringType((StringType) typeValue);

            if (typeValue.getClass().equals(Attachment.class))
                return fromAttachment((Attachment) typeValue);

            if (typeValue.getClass().equals(DateType.class))
                return fromDateType((DateType) typeValue);

            if (typeValue.getClass().equals(Age.class))
                return fromAge((Age) typeValue);

            if (typeValue.getClass().equals(DateTimeType.class))
                return fromDateTimeType((DateTimeType) typeValue);

            if (typeValue.getClass().equals(Boolean.class))
                return fromBoolean((Boolean) typeValue);

            if (typeValue.getClass().equals(BooleanType.class))
                return fromBooleanType((BooleanType) typeValue);

            if (typeValue.getClass().equals(Range.class))
                return fromRange((Range) typeValue);

            if (typeValue.getClass().equals(DecimalType.class))
                return fromDecimalType((DecimalType) typeValue);

            if (typeValue.getClass().equals(Reference.class))
                return fromReference((Reference) typeValue);

            if (typeValue.getClass().equals(Annotation.class))
                return fromAnnotation((Annotation) typeValue);

            if (typeValue.getClass().equals(Duration.class))
                return fromDuration((Duration) typeValue);

            if (typeValue.getClass().equals(Dosage.class))
                return fromDosage((Dosage) typeValue);


        } catch (Exception ex) {
            logger.error(String.format("Unable to parse type-value %s of type %s.", typeValue, typeValue.getClass().getCanonicalName()), ex);
        }
        return typeValue.toString();
    }

    private static String fromDuration(Duration typeValue) {
        return typeValue.getValue() + " " + getPeriodUnitFullName(typeValue.getCode());
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
        String dateString = DateUtil.toDateString(typeValue, DATE_FORMAT);
        if (dateString.endsWith(" 00:00"))
            return dateString.substring(0, dateString.length() - 6);
        return dateString;
    }

    private static String fromDateType(DateType typeValue) {
        Date value = typeValue.getValue();
        return fromJavaUtilDate(value);
    }

    private static String fromDecimalType(DecimalType typeValue) {
        return typeValue.getValueAsString();
    }

    private static String fromAttachment(Attachment typeValue) {
        return typeValue.getUrl();
    }

    private static String fromStringType(StringType typeValue) {
        return typeValue.getValue();
    }

    private static String fromSampleData(SampledData typeValue) {
        return typeValue.getData();
    }

    private static String fromPeriod(Period typeValue) {
        if (typeValue.getStart() == null && typeValue.getEnd() == null) return INVALID_PERIOD;
        String start = (typeValue.getStart() != null) ? fromJavaUtilDate(typeValue.getStart()) : PERIOD_START_UNKNOWN;
        String end;
        if (typeValue.getEnd() == null)
            return start;
        else
            end = fromJavaUtilDate(typeValue.getEnd());

        return start + " - " + end;
    }

    private static String fromRatio(Ratio typeValue) {
        Quantity numerator = typeValue.getNumerator();
        Quantity denominator = typeValue.getDenominator();
        if (numerator == null || numerator.getValue() == null) return "0";
        if (denominator == null || denominator.getValue() == null) return "0";
        return numerator.getValue() + "/" + denominator.getValue();
    }

    private static String fromCodeableConcept(CodeableConcept typeValue) {
        if (!typeValue.getCoding().isEmpty())
            return typeValue.getCoding().get(0).getDisplay();
        return typeValue.getText() != null ? typeValue.getText() : "";
    }

    private static String fromQuantity(Quantity typeValue) {
        BigDecimal value = typeValue.getValue();
        String units = typeValue.getUnit();
        String code = typeValue.getCode();

        if (value == null) return "";
        if (units != null) return value + " " + units;
        if (code != null) return value + " " + code;
        return String.valueOf(value);
    }

    private static String fromDateTimeType(DateTimeType typeValue) {
        Date value = typeValue.getValue();
        return fromJavaUtilDate(value);
    }

    private static String fromAge(Age typeValue) {
        return typeValue.getValue() + " yrs";
    }

    private static String fromBooleanType(BooleanType typeValue) {
        return Boolean.TRUE.equals(typeValue.getValue()) ? "YES" : "NO";
    }

    private static String fromRange(Range typeValue) {
        SimpleQuantity low = typeValue.getLow();
        SimpleQuantity high = typeValue.getHigh();
        String lowerRange = (low != null) ? fromQuantity(low) : "";
        String higherRange = (high != null) ? fromQuantity(high) : "";
        return lowerRange + " - " + higherRange;
    }

    private static String fromReference(Reference typeValue) {
        String reference = typeValue.getReference();
        return StringUtils.isBlank(reference) ? INVALID_REFERENCE : reference;
    }

    private static String fromAnnotation(Annotation typeValue) {
        return typeValue.getText();
    }

    private static String fromDosage(Dosage dosage) {
        Timing timing = dosage.getTiming();
        String result = "";
        Timing.TimingRepeatComponent repeat = timing.getRepeat();
        if (repeat == null) return "timing not specified";
        Integer frequency = repeat.getFrequency();
        List<Enumeration<Timing.EventTiming>> when = repeat.getWhen();
        BigDecimal period = repeat.getPeriod();
        if (period != null && repeat.getPeriodUnit() != null) {
            result = period + " " + getPeriodUnitFullName(repeat.getPeriodUnit().toCode());
        }
        if (frequency != 0) {
            result = frequency + " time(s) in " + result;
        } else if (!CollectionUtils.isEmpty(when)) {
            result += " " + getEventTimingFullName(when.get(0).getValue().toCode());
        } else {
            result += " " + getCustomDosage(dosage);
        }

        String bound = convertToText(repeat.getBounds());
        if (bound != null) {
            result += ". Duration:- " + bound;
        }
        return result;
    }

    private static String getCustomDosage(Dosage dosage) {
        List<Extension> extensions = dosage.getExtensionsByUrl(CUSTOM_DOSAGE_EXTENSION_URL);
        if (CollectionUtils.isEmpty(extensions)) return "";
        Extension extension = extensions.get(0);
        String value = ((StringType) extension.getValue()).getValue();
        try {
            Map<String, Double> map = new ObjectMapper().readValue(value, Map.class);
            Double morningDose = map.containsKey(MORNING_DOSE) ? map.get(MORNING_DOSE) : Double.valueOf(0);
            Double afternoonDose = map.containsKey(AFTERNOON_DOSE) ? map.get(AFTERNOON_DOSE) : Double.valueOf(0);
            Double eveningDose = map.containsKey(EVENING_DOSE) ? map.get(EVENING_DOSE) : Double.valueOf(0);
            return String.format("Morning:-%s, Afternoon:-%s, Evening:-%s", morningDose, afternoonDose, eveningDose);
        } catch (IOException e) {
            logger.error(String.format("Can not read decide dosage for %s", value), e);
        }
        return "";
    }


    private static String getPeriodUnitFullName(String unitKey) {
        List<String> unitValues = asList("Second", "Minute", "Hour", "Day", "Week", "Month", "Year");
        List<String> unitKeys = asList("s", "min", "h", "d", "wk", "mo", "a");
        int index = unitKeys.indexOf(unitKey);
        return unitValues.get(index);
    }

    private static String getEventTimingFullName(String timingKey) {
        List<String> timingKeys = asList("HS", "WAKE", "C", "CM", "CD", "CV", "AC", "ACM", "ACD", "ACV", "PC", "PCM", "PCD", "PCV");
        List<String> timingValues = asList("before sleep", "after Waking up", "with meal", "with breakfast",
                "with lunch", "with dinner", "before meal", "before breakfast", "before lunch",
                "before dinner", "after meal", "after breakfast", "after lunch", "after dinner");
        int index = timingKeys.indexOf(timingKey);
        return timingValues.get(index);
    }
}
