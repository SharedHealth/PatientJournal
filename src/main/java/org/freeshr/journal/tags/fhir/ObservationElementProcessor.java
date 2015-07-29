package org.freeshr.journal.tags.fhir;


import org.hl7.fhir.instance.model.Observation;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Text;
import org.thymeleaf.processor.element.AbstractMarkupSubstitutionElementProcessor;
import org.thymeleaf.spring4.naming.SpringContextVariableNames;
import org.thymeleaf.standard.expression.VariableExpression;

import java.util.ArrayList;
import java.util.Collections;
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
        List<Observation> observations = (List<Observation>) arguments.getLocalVariable(expression);
        if (observations.isEmpty()) return Collections.emptyList();
        return executeEncounterBundle(observations);
    }

    private List<Node> executeEncounterBundle(List<Observation> observations) {
        List<Node> nodes = new ArrayList<>();
        Element table = new Element("table");
        table.setAttribute("class", "table-observation");

        for (Observation observation : observations) {
            if (isChildObservation(observation, observations)) continue;
            Element tbody = new Element("tbody");
            processObservation(observation, observations, 0, tbody);
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

    private void processObservation(Observation observation, List<Observation> observations, int depth, Element observationBody) {
        Element tr = new Element("tr");
        tr.setAttribute("class", "level" + depth);

        String name = convertToText(observation.getName());
        Element nameTd = new Element("td");
        nameTd.setAttribute("class","bolder-text");
        nameTd.addChild(new Text(name));

        Element valueTd = new Element("td");

        if (observation.getValue() != null) {
            Element valueSpan = new Element("span");
            valueSpan.addChild(new Text(convertToText(observation.getValue())));
            valueTd.addChild(valueSpan);
        }
        if (observation.getInterpretation() != null) {
            valueTd.addChild(createSpanForTab());
            valueTd.addChild(createBoldSpanFor("Interpretation:- "));
            Element interpretationSpan = new Element("span");
            interpretationSpan.addChild(new Text(convertToText(observation.getInterpretation())));
            valueTd.addChild(interpretationSpan);
        }
        if (observation.getComments() != null) {
            valueTd.addChild(createSpanForTab());
            valueTd.addChild(createBoldSpanFor("Comments:- "));
            Element commentsSpan = new Element("span");
            commentsSpan.addChild(new Text(observation.getCommentsSimple()));
            valueTd.addChild(commentsSpan);
        }

        tr.addChild(nameTd);
        tr.addChild(valueTd);

        observationBody.addChild(tr);

        if (observation.getRelated().isEmpty()) return;
        depth++;
        for (Observation.ObservationRelatedComponent observationRelatedComponent : observation.getRelated()) {
            Observation childObservation = findChildObservation(observationRelatedComponent.getTarget().getReferenceSimple(), observations);
            if (childObservation != null) {
                processObservation(childObservation, observations, depth, observationBody);
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

    private Observation findChildObservation(String referenceSimple, List<Observation> observations) {
        for (Observation observation : observations) {
            if (referenceSimple.equals(observation.getIdentifier().getValueSimple()))
                return observation;
        }
        return null;
    }

    private boolean isChildObservation(Observation observation, List<Observation> observations) {
        for (Observation obs : observations) {
            for (Observation.ObservationRelatedComponent observationRelatedComponent : obs.getRelated()) {
                if (observationRelatedComponent.getTarget().getReferenceSimple().equals(observation.getIdentifier().getValueSimple()))
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
