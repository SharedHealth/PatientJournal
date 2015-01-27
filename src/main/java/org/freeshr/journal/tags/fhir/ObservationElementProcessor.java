package org.freeshr.journal.tags.fhir;


import org.freeshr.journal.model.EncounterBundle;
import org.hl7.fhir.instance.model.Observation;
import org.hl7.fhir.instance.model.Resource;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Text;
import org.thymeleaf.processor.element.AbstractMarkupSubstitutionElementProcessor;
import org.thymeleaf.spring4.naming.SpringContextVariableNames;
import org.thymeleaf.standard.expression.VariableExpression;

import java.util.ArrayList;
import java.util.List;

public class ObservationElementProcessor extends AbstractMarkupSubstitutionElementProcessor {

    protected ObservationElementProcessor() {
        super("observation");
    }

    @Override
    protected List<Node> getMarkupSubstitutes(Arguments arguments, Element element) {
        String expression = ((VariableExpression) arguments.getLocalVariable(SpringContextVariableNames.SPRING_BOUND_OBJECT_EXPRESSION)).getExpression();
        List<Observation> observations = (List<Observation>) arguments.getLocalVariable(expression);
        return executeEncounterBundle(observations);
    }

    private List<Node> executeEncounterBundle(List<Observation> observations) {
        List<Node> nodes = new ArrayList<>();
        Element ul = new Element("ul");
        for (Observation observation : observations) {
            if (isChildObservation(observation, observations)) continue;
            Element li = processRootObservation(observation, observations);
            ul.addChild(li);
        }
        nodes.add(ul);
        return nodes;
    }

    private Element processRootObservation(Observation observation, List<Observation> observations) {
        Element li = new Element("li");
        String name = observation.getName().getCoding().get(0).getDisplaySimple();
        final Text text = new Text(name);
        li.addChild(text);
        if (observation.getRelated().isEmpty()) return li;
        Element ul = new Element("ul");
        for (Observation.ObservationRelatedComponent observationRelatedComponent : observation.getRelated()) {
            Observation childObservation = findChildObservation(observationRelatedComponent.getTarget().getReferenceSimple(), observations);
            if (childObservation != null) {
                ul.addChild(processRootObservation(childObservation, observations));
            }
        }
        li.addChild(ul);
        return li;
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
