package org.freeshr.journal.tags.fhir;


import org.freeshr.journal.model.EncounterBundle;
import org.hl7.fhir.instance.model.Observation;
import org.hl7.fhir.instance.model.Resource;
import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Element;
import org.thymeleaf.dom.Node;
import org.thymeleaf.dom.Text;
import org.thymeleaf.processor.element.AbstractMarkupSubstitutionElementProcessor;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;

import java.util.ArrayList;
import java.util.List;

public class ObservationElementProcessor extends AbstractMarkupSubstitutionElementProcessor {

    protected ObservationElementProcessor() {
        super("observation");
    }

    @Override
    protected List<Node> getMarkupSubstitutes(Arguments arguments, Element element) {
        final Configuration configuration = arguments.getConfiguration();
        final IStandardExpressionParser parser =
                StandardExpressions.getExpressionParser(configuration);

        final IStandardExpression expression =
                parser.parseExpression(configuration, arguments, "${bundle}");


        EncounterBundle encounterBundle = (EncounterBundle) expression.execute(configuration, arguments);
        return executeEncounterBundle(encounterBundle);
    }

    private List<Node> executeEncounterBundle(EncounterBundle encounterBundle) {
        List<Node> nodes = new ArrayList<>();
        Element ul = new Element("ul");
        for (Resource resource : encounterBundle.getResources()) {
            if (!resource.getResourceType().getPath().equals("observation")) continue;
            Observation observation = (Observation) resource;
            if (isChildObservation(observation, encounterBundle)) continue;
            Element li = processRootObservation(observation, encounterBundle);
            ul.addChild(li);
        }
        nodes.add(ul);
        return nodes;
    }

    private Element processRootObservation(Observation observation, EncounterBundle encounterBundle) {
        Element li = new Element("li");
        String name = observation.getName().getCoding().get(0).getDisplaySimple();
        final Text text = new Text(name);
        li.addChild(text);
        if (observation.getRelated().isEmpty()) return li;
        Element ul = new Element("ul");
        for (Observation.ObservationRelatedComponent observationRelatedComponent : observation.getRelated()) {
            Observation childObservation = findChildObservation(observationRelatedComponent.getTarget().getReferenceSimple(), encounterBundle);
            if (childObservation != null) {
                ul.addChild(processRootObservation(childObservation, encounterBundle));
            }
        }
        li.addChild(ul);
        return li;
    }

    private Observation findChildObservation(String referenceSimple, EncounterBundle encounterBundle) {
        for (Resource resource : encounterBundle.getResources()) {
            if (!resource.getResourceType().getPath().equals("observation")) continue;
            Observation obs = (Observation) resource;
            if (referenceSimple.equals(obs.getIdentifier().getValueSimple()))
                return obs;
        }
        return null;
    }

    private boolean isChildObservation(Observation observation, EncounterBundle encounterBundle) {
        for (Resource resource : encounterBundle.getResources()) {
            if (!resource.getResourceType().getPath().equals("observation")) continue;
            Observation obs = (Observation) resource;
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
