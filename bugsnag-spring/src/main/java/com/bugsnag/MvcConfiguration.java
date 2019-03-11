package com.bugsnag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * If spring-webmvc is loaded, add configuration for reporting unhandled exceptions.
 */
@Configuration
@Conditional(SpringWebMvcLoadedCondition.class)
class MvcConfiguration {

    @Autowired
    private Bugsnag bugsnag;

    /**
     * Register an exception resolver to send unhandled reports to Bugsnag
     * for uncaught exceptions thrown from request handlers.
     */
    @Bean
    BugsnagMvcExceptionHandler bugsnagHandlerExceptionResolver() {
        return new BugsnagMvcExceptionHandler(bugsnag);
    }

    /**
     * Add a callback to assign specified severities for some Spring exceptions.
     */
    @PostConstruct
    void addExceptionClassCallback() {
        bugsnag.addCallback(new ExceptionClassCallback());
    }
}
