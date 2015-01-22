package org.freeshr.journal.tags.fhir;


import org.freeshr.journal.tags.AttributeProcessor;
import org.hl7.fhir.instance.model.*;

public class ObsValueAttrProcessor extends AttributeProcessor<Type> {
    protected ObsValueAttrProcessor() {
        super("obsvalue");
    }

    @Override
    protected String process(Type obsValue) {
        if (obsValue == null)
            return "";
        if (obsValue instanceof Quantity)
            return getObsValueFromQuantity((Quantity) obsValue);
        if (obsValue instanceof CodeableConcept)
            return getObsValueFromCodeableConcept((CodeableConcept) obsValue);
        if (obsValue instanceof Ratio)
            return getObsValueFromRatio((Ratio) obsValue);
        if (obsValue instanceof Period)
            return getObsValueFromPeriod((Period) obsValue);
        if (obsValue instanceof SampledData)
            return getObsValueFromSampleData((SampledData) obsValue);
        if (obsValue instanceof String_)
            return getObsValueFromString_((String_) obsValue);
        if (obsValue instanceof Attachment)
            return getObsValueFromAttachment((Attachment) obsValue);
        if (obsValue instanceof Decimal)
            return getObsValueFromDecimal((Decimal) obsValue);
        if (obsValue instanceof Date)
            return getObsValueFromDate((Date) obsValue);

        return "Unknown";

    }

    private String getObsValueFromDate(Date obsValue) {
        return obsValue.getValue().toHumanDisplay();
    }

    private String getObsValueFromDecimal(Decimal obsValue) {
        return obsValue.getStringValue();
    }

    private String getObsValueFromAttachment(Attachment obsValue) {
        return obsValue.getUrlSimple();
    }

    private String getObsValueFromString_(String_ obsValue) {
        return obsValue.getValue();
    }

    private String getObsValueFromSampleData(SampledData obsValue) {
        return obsValue.getDataSimple();
    }

    private String getObsValueFromPeriod(Period obsValue) {
        return obsValue.getStartSimple().toHumanDisplay() + " to " + obsValue.getEndSimple().toHumanDisplay();
    }

    private String getObsValueFromRatio(Ratio obsValue) {
        return obsValue.getNumerator().getValueSimple() + "/" + obsValue.getDenominator().getValueSimple();
    }

    private String getObsValueFromCodeableConcept(CodeableConcept obsValue) {
        return  obsValue.getCoding().get(0).getDisplaySimple();
    }

    private String getObsValueFromQuantity(Quantity obsValue) {
        return obsValue.getValueSimple() + " " + obsValue.getUnitsSimple();
    }
}
