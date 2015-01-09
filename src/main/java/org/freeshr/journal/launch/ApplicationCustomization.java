package org.freeshr.journal.launch;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.stereotype.Component;

import java.util.Map;

import static java.lang.System.getenv;

@Component
public class ApplicationCustomization implements EmbeddedServletContainerCustomizer {
    @Override
    public void customize(ConfigurableEmbeddedServletContainer container) {
        Map<String, String> env = getenv();
        String patient_journal_port = env.get("PATIENT_JOURNAL_PORT");
        int port = StringUtils.isEmpty(patient_journal_port) ? 8090 : Integer.parseInt(patient_journal_port);
        container.setPort(port);
    }
}
