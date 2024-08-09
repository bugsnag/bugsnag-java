package com.bugsnag;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

/**
 * If spring-webmvc is loaded, add configuration for reporting unhandled exceptions.
 */
@Configuration
@Conditional(SpringWebMvcLoadedCondition.class)
class JavaxMvcConfiguration implements InitializingBean {

    @Autowired
    private Bugsnag bugsnag;

    /**
     * Register an exception resolver to send unhandled reports 
     * to Bugsnag
     * for uncaught exceptions thrown from request handlers.
     */
    @Bean
    BugsnagJavaxMvcExceptionHandler bugsnagHandlerExceptionResolver() {
        return new BugsnagJavaxMvcExceptionHandler(bugsnag);
    }

    /**
     * Add a callback to assign specified severities for some Spring exceptions.
     */
    @Override
    public void afterPropertiesSet() {
        bugsnag.addCallback(new ExceptionClassCallback());
    }
}
