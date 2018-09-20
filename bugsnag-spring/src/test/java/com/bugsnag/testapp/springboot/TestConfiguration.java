package com.bugsnag.testapp.springboot;

import com.bugsnag.Bugsnag;
import com.bugsnag.BugsnagSpringConfiguration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * This test configuration loads the BugsnagSpringConfiguration
 * that will be used for real Spring bugsnag integration.
 */
@Configuration
@Import(BugsnagSpringConfiguration.class)
public class TestConfiguration {
    @Bean
    public Bugsnag bugsnag() {
        return Bugsnag.init("apiKey");
    }
}
