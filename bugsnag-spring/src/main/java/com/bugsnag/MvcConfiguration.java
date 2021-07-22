package com.bugsnag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;
import javax.annotation.PostConstruct;

/**
 * If spring-webmvc is loaded, add configuration for reporting unhandled exceptions.
 */
@Configuration
@Conditional(SpringWebMvcLoadedCondition.class)
class MvcConfiguration extends WebMvcConfigurerAdapter {

    @Autowired
    private Bugsnag bugsnag;

    /**
     * Add a callback to assign specified severities for some Spring exceptions.
     */
    @PostConstruct
    void addExceptionClassCallback() {
        bugsnag.addCallback(new ExceptionClassCallback());
    }

    /**
     * Normally, the exceptionResolvers contain the following resolvers in this order:
     * - {@link org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver}
     * - {@link org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver}
     * - {@link org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver}
     * <p>
     * The first two handlers handle exceptions in an application-specific manner.
     * (either by @{@link org.springframework.web.bind.annotation.ExceptionHandler}
     * or @{@link org.springframework.web.bind.annotation.ResponseStatus})
     * <p>
     * Therefore, exceptions that are handled by these handlers should not be handled by Bugsnag.
     * Only unhandled exceptions shall be sent to Bugsnag.
     */
    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        final int position = exceptionResolvers.isEmpty() ? 0 : exceptionResolvers.size() - 1;
        exceptionResolvers.add(position, new BugsnagMvcExceptionHandler(bugsnag));
    }
}
