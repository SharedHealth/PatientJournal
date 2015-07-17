package org.freeshr.journal.tags.fhir;

import org.freeshr.journal.tags.AttributeProcessor;
import org.freeshr.journal.tags.TypeConverter;
import org.hl7.fhir.instance.model.Type;

import static org.freeshr.journal.tags.TypeConverter.convertToText;

public class FHIRTypeProcessor extends AttributeProcessor<Type> {

    public FHIRTypeProcessor() {
        super("type");
    }

    @Override
    protected String process(Type onset) {
        return convertToText(onset);
    }


}


