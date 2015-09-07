package org.freeshr.journal.tags.fhir;


import ca.uhn.fhir.model.api.IResource;
import ca.uhn.fhir.model.dstu2.composite.BoundCodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.ResourceReferenceDt;
import ca.uhn.fhir.model.dstu2.resource.Composition;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.valueset.ObservationInterpretationCodesEnum;
import ca.uhn.fhir.model.primitive.IdDt;
import org.freeshr.journal.model.EncounterBundleData;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Text;
import org.thymeleaf.processor.element.AbstractMarkupSubstitutionElementProcessor;
import org.thymeleaf.spring4.naming.SpringContextVariableNames;
import org.thymeleaf.standard.expression.VariableExpression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.freeshr.journal.tags.TypeConverter.convertToText;

public class ObservationElementProcessor extends AbstractMarkupSubstitutionElementProcessor {

    protected ObservationElementProcessor() {
        super("observation");
    }

    @Override
    @SuppressWarnings("uncheck")
    protected List<Node> getMarkupSubstitutes(Arguments arguments, Element element) {
        String expression = ((VariableExpression) arguments.getLocalVariable(SpringContextVariableNames.SPRING_BOUND_OBJECT_EXPRESSION)).getExpression();
        EncounterBundleData encounterBundle = (EncounterBundleData) arguments.getLocalVariable(expression);
        if (encounterBundle == null || CollectionUtils.isEmpty(encounterBundle.getObservations()))
            return Collections.emptyList();
        List<Observation> topLevelObs = identifyTopLevelResourcesOfTypeByExclusion(encounterBundle, Observation.class);
        return executeEncounterBundle(topLevelObs, encounterBundle.getObservations());
    }

    private List<Node> executeEncounterBundle(List<Observation> topLevelObs, List<Observation> allObs) {
        List<Node> nodes = new ArrayList<>();
        Element table = new Element("table");
        table.setAttribute("class", "table-observation");

        for (Observation observation : topLevelObs) {
            Element tbody = new Element("tbody");
            processObservation(observation, allObs, 0, tbody);
            table.addChild(tbody);
        }
        Element collapseExpandSpan = new Element("span");
        collapseExpandSpan.setAttribute("class", "span-exp-col");
        collapseExpandSpan.addChild(new Text("+"));
        nodes.add(collapseExpandSpan);

        Element span = new Element("span");
        span.setAttribute("class", "div-resource-head");
        span.addChild(new Text(" Observations"));
        nodes.add(span);

        Element divContent = new Element("div");
        divContent.setAttribute("class", "div-resource-content");
        divContent.addChild(table);
        nodes.add(divContent);

        return nodes;
    }

    private void processObservation(Observation observation, List<Observation> allObs, int depth, Element observationBody) {
        Element tr = new Element("tr");
        tr.setAttribute("class", "level" + depth);

        String name = convertToText(observation.getCode());
        Element nameTd = new Element("td");
        nameTd.setAttribute("class", "bolder-text");
        nameTd.addChild(new Text(name));

        Element valueTd = new Element("td");

        if (observation.getValue() != null) {
            Element valueSpan = new Element("span");
            valueSpan.addChild(new Text(convertToText(observation.getValue())));
            valueTd.addChild(valueSpan);
        }
        BoundCodeableConceptDt<ObservationInterpretationCodesEnum> interpretation = observation.getInterpretation();
        if (interpretation != null && (!CollectionUtils.isEmpty(interpretation.getCoding()))) {
            valueTd.addChild(createSpanForTab());
            valueTd.addChild(createBoldSpanFor("Interpretation:- "));
            Element interpretationSpan = new Element("span");
            interpretationSpan.addChild(new Text(convertToText(interpretation)));
            valueTd.addChild(interpretationSpan);
        }
        if (observation.getComments() != null) {
            valueTd.addChild(createSpanForTab());
            valueTd.addChild(createBoldSpanFor("Comments:- "));
            Element commentsSpan = new Element("span");
            commentsSpan.addChild(new Text(observation.getComments()));
            valueTd.addChild(commentsSpan);
        }

        tr.addChild(nameTd);
        tr.addChild(valueTd);

        observationBody.addChild(tr);

        if (observation.getRelated().isEmpty()) return;
        depth++;
        for (Observation.Related related : observation.getRelated()) {
            IdDt reference = related.getTarget().getReference();
            if (reference == null) continue;
            Observation childObservation = findChildObservation(reference.getValue(), allObs);
            if (childObservation != null) {
                processObservation(childObservation, allObs, depth, observationBody);
            }
        }
    }

    private Element createSpanForTab() {
        Element tabSpan = new Element("span");
        tabSpan.addChild(new Text("    |    "));
        return tabSpan;
    }

    private Element createBoldSpanFor(String key) {
        Element valueSpan = new Element("span");
        Element strong = new Element("strong");
        strong.addChild(new Text(key));
        valueSpan.addChild(strong);
        return valueSpan;
    }

    private Observation findChildObservation(String referenceSimple, List<Observation> allObs) {
        for (Observation observation : allObs) {
            if (referenceSimple.equals(observation.getId().getValue()))
                return observation;
        }
        return null;
    }

    private <T extends IResource> List<T> identifyTopLevelResourcesOfTypeByExclusion(EncounterBundleData encounterBundle, Class<T> type) {
        List<IResource> allResources = encounterBundle.getEncounterBundle().getResources();
        List<ResourceReferenceDt> childResourceReferences = new ArrayList<>();
        for (IResource resource : allResources) {
            if (resource instanceof Composition || resource instanceof Encounter) continue;
            childResourceReferences.addAll(resource.getAllPopulatedChildElementsOfType(ResourceReferenceDt.class));
        }
        HashSet<ResourceReferenceDt> childReferences = new HashSet<>();
        childReferences.addAll(childResourceReferences);

        ArrayList<T> topLevelResources = new ArrayList<>();

        for (IResource resource : allResources) {
            if (type.isInstance(resource)) {
                if (!isChildReference(childReferences, resource.getId().getValue())) {
                    topLevelResources.add((T) resource);
                }
            }
        }
        return topLevelResources;
    }

    private boolean isChildReference(HashSet<ResourceReferenceDt> childReferenceDts, String resourceRef) {
        for (ResourceReferenceDt childRef : childReferenceDts) {
            if (!childRef.getReference().isEmpty() && childRef.getReference().getValue().equals(resourceRef)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public int getPrecedence() {
        return 1000;
    }
}
