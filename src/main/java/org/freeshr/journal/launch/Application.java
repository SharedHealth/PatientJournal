package org.freeshr.journal.launch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.util.Map;

import static java.lang.System.getenv;

@Import(ApplicationConfiguration.class)
@ComponentScan({"org.freeshr.journal"})
@EnableAutoConfiguration
public class Application {

    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }
}