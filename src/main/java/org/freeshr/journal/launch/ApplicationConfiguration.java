package org.freeshr.journal.launch;

import org.freeshr.journal.tags.fhir.FHIRDialect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.thymeleaf.dialect.IDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import java.util.HashSet;

@Import(ApplicationProperties.class)
@Configuration
public class ApplicationConfiguration {
    
    @Bean
    public FHIRDialect fhirDialect(){
        return new FHIRDialect();
    }

    @Bean
    public TemplateResolver templateResolver(){
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine(){
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver());
        HashSet<IDialect> set = new HashSet<>();
        set.add(fhirDialect());
        engine.setAdditionalDialects(set);
        return engine;
    }
}
