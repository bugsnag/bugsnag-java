package com.bugsnag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AutoConfiguration {

    @Autowired
    private Bugsnag bugsnag;

    @Bean
    @ConditionalOnMissingBean
    public BugsnagHandlerExceptionResolver bugsnagHandlerExceptionResolver() {
        return new BugsnagHandlerExceptionResolver(bugsnag);
    }
}
