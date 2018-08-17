package com.bugsnag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringConfiguration {

    @Autowired
    private Bugsnag bugsnag;

    @Bean
    public BugsnagHandlerExceptionResolver bugsnagHandlerExceptionResolver() {
        return new BugsnagHandlerExceptionResolver(bugsnag);
    }
}
