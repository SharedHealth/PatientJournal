package org.freeshr.journal.tags.fhir;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

import java.util.HashSet;
import java.util.Set;

public class FHIRDialect extends AbstractDialect{
    public FHIRDialect() {
        super();
    }
    
    @Override
    public String getPrefix() {
        return "fhir";
    }

    @Override
    public Set<IProcessor> getProcessors() {
        final Set<IProcessor> processors = new HashSet<IProcessor>();
        processors.add(new OnSetAttrProcessor());
        processors.add(new ObservationElementProcessor());
        return processors;
    }
}
