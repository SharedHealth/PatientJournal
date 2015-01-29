package org.freeshr.journal.tags.fhir;


import org.hl7.fhir.instance.model.*;
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
        Element thead = new Element("thead");
        table.addChild(thead);

        Element headTr = new Element("tr");

        Element nameTh = new Element("th");
        nameTh.addChild(new Text("Observation"));
        nameTh.setAttribute("class","col-observation");
        headTr.addChild(nameTh);

        Element valueTh = new Element("th");
        valueTh.addChild(new Text("Value"));
        valueTh.setAttribute("class","col-value");
        headTr.addChild(valueTh);

        Element interpretationTh = new Element("th");
        interpretationTh.addChild(new Text("Interpretation"));
        interpretationTh.setAttribute("class","col-interpretation");
        headTr.addChild(interpretationTh);

        Element commentsTh = new Element("th");
        commentsTh.addChild(new Text("Comments"));
        commentsTh.setAttribute("class","col-comments");
        headTr.addChild(commentsTh);

        thead.addChild(headTr);


        for (Observation observation : observations) {
            if (isChildObservation(observation, observations)) continue;
            Element tbody = new Element("tbody");
            processObservation(observation, observations, 0, tbody);
            table.addChild(tbody);
        }
        nodes.add(table);
        return nodes;
    }

    private void processObservation(Observation observation, List<Observation> observations, int depth, Element observationBody) {
        Element tr = new Element("tr");
        tr.setAttribute("class", "level" + depth);

        String name = observation.getName().getCoding().get(0).getDisplaySimple();
        Element nameTd = new Element("td");
        nameTd.setAttribute("class","col-observation");
        nameTd.addChild(new Text(name));

        String value = getValue(observation.getValue());
        Element valueTd = new Element("td");
        valueTd.setAttribute("class","col-value");
        valueTd.addChild(new Text(value));

        String interpretation = (observation.getInterpretation() != null) ? observation.getInterpretation().getCoding().get(0).getDisplaySimple() : "";
        Element interpretationTd = new Element("td");
        interpretationTd.setAttribute("class","col-interpretation");
        interpretationTd.addChild(new Text(interpretation));

        String comments = (observation.getCommentsSimple() != null) ? observation.getCommentsSimple() : "";
        Element commentsTd = new Element("td");
        commentsTd.setAttribute("class","col-comments");
        commentsTd.addChild(new Text(comments));

        tr.addChild(nameTd);
        tr.addChild(valueTd);
        tr.addChild(interpretationTd);
        tr.addChild(commentsTd);

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

    private String getValue(Type obsValue) {
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
        return obsValue.getCoding().get(0).getDisplaySimple();
    }

    private String getObsValueFromQuantity(Quantity obsValue) {
        return obsValue.getValueSimple() + " " + obsValue.getUnitsSimple();
    }

    @Override
    public int getPrecedence() {
        return 1000;
    }
}
