package com.bugsnag;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(BugsnagSpringConfiguration.class)
@ComponentScan(basePackages = {"com.bugsnag.controller"})
public class TestConfiguration {
    @Bean
    public Bugsnag bugsnag() {
        return Mockito.mock(Bugsnag.class);
    }
}
