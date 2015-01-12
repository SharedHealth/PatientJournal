package org.freeshr.journal.controller;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.mock.env.MockPropertySource;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class EnvironmentMock implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private Map<String, String> mockPropertySources(ConfigurableApplicationContext applicationContext) {
        MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();
        MockPropertySource mockEnvVars = new MockPropertySource();
        Map<String, String> env = new HashMap<String, String>();
        try {
            InputStream inputStream = this.getClass().getResourceAsStream("/local.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            for (Object property : properties.keySet()) {
                mockEnvVars.setProperty(property.toString(), properties.getProperty(property.toString()));
                env.put(property.toString(), properties.getProperty(property.toString()));
            }
            propertySources.replace(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, mockEnvVars);
        } catch (Exception ignored) {
        }
        return env;
    }

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        mockPropertySources(applicationContext);
    }
}
