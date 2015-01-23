package org.freeshr.journal.controller;

public class ExternalRef {
    private final String templateName;
    private final String modelName;
    private final Object data;

    public ExternalRef(String templateName, String modelName, Object data) {
        this.templateName = templateName;
        this.modelName = modelName;
        this.data = data;
    }

    public String getTemplateName() {
        return templateName;
    }

    public String getModelName() {
        return modelName;
    }

    public Object getData() {
        return data;
    }
}
