package com.bugsnag;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * This test configuration loads the BugsnagSpringConfiguration
 * that will be used for real Spring bugsnag integration.
 */
@EnableWebMvc
@Configuration
@Import(BugsnagSpringConfiguration.class)
@ComponentScan(basePackages = {"com.bugsnag.controller"})
public class TestConfiguration {
    @Bean
    public Bugsnag bugsnag() {
        Bugsnag bugsnag = new Bugsnag("apiKey");
        return Mockito.spy(bugsnag);
    }
}
