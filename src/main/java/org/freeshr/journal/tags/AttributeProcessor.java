package org.freeshr.journal.tags;

import org.thymeleaf.Arguments;
import org.thymeleaf.Configuration;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.IAttributeNameProcessorMatcher;
import org.thymeleaf.processor.attr.AbstractTextChildModifierAttrProcessor;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;


public abstract class AttributeProcessor<T> extends AbstractTextChildModifierAttrProcessor {
    
    protected AttributeProcessor(IAttributeNameProcessorMatcher matcher) {
        super(matcher);
    }

    protected AttributeProcessor(String attributeName) {
        super(attributeName);
    }

    @Override
    protected String getText(Arguments arguments, Element element, String attributeName) {
        final Configuration configuration = arguments.getConfiguration();
        final String attributeValue = element.getAttributeValue(attributeName);
        /*
         * Obtain the Thymeleaf Standard Expression parser
         */
        final IStandardExpressionParser parser =
                StandardExpressions.getExpressionParser(configuration);

        /*
         * Parse the attribute value as a Thymeleaf Standard Expression
         */
        final IStandardExpression expression =
                parser.parseExpression(configuration, arguments, attributeValue);

        /*
         * Execute the expression just parsed
         */

        T object = (T) expression.execute(configuration, arguments);
        return process(object);
    }

    abstract protected String process(T type);

    @Override
    public int getPrecedence() {
        return 1000;
    }
}
