package org.freeshr.journal.launch;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import(ApplicationProperties.class)
@Configuration
public class ApplicationConfiguration {

}
