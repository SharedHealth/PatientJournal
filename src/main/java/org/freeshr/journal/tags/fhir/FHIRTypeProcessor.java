package org.freeshr.journal.tags.fhir;

import org.freeshr.journal.tags.AttributeProcessor;

import static org.freeshr.journal.tags.TypeConverter.convertToText;

public class FHIRTypeProcessor extends AttributeProcessor<Object> {

    public FHIRTypeProcessor() {
        super("type");
    }

    @Override
    protected String process(Object typeValue) {
        return convertToText(typeValue);
    }


}


