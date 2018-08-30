package com.bugsnag;

import com.bugsnag.callbacks.Callback;

import org.springframework.boot.SpringBootVersion;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.SpringVersion;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.annotation.PostConstruct;

/**
 * Configuration to integrate Bugsnag with Spring.
 */
@Configuration
public class BugsnagSpringConfiguration {

    private final Bugsnag bugsnag;

    public BugsnagSpringConfiguration(final Bugsnag bugsnag) {
        this.bugsnag = bugsnag;
    }

    /**
     * Add a callback to add the version of Spring used by the application
     */
    @Bean
    public Callback springVersionCallback() {
        Callback callback = new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.addToTab("device", "springVersion", SpringVersion.getVersion());
            }
        };
        bugsnag.addCallback(callback);
        return callback;
    }

    /**
     * If Spring Boot is used, add a callback to add the version of Spring used
     * by the application.
     */
    @Bean
    @Conditional(SpringBootLoadedCondition.class)
    public Callback springBootVersionCallback() {
        Callback callback = new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.addToTab("device", "springBootVersion", SpringBootVersion.getVersion());
            }
        };
        bugsnag.addCallback(callback);
        return callback;
    }

    /**
     * If spring-webmvc is loaded, add configuration for reporting unhandled exceptions
     * and session tracking.
     */
    @Configuration
    @Conditional(SpringWebMvcLoadedCondition.class)
    public class SpringWebMvcConfiguration extends WebMvcConfigurerAdapter {

        /**
         * Register an exception resolver to send unhandled reports to Bugsnag
         * for uncaught exceptions thrown from request handlers.
         */
        @Bean
        public BugsnagHandlerExceptionResolver bugsnagHandlerExceptionResolver() {
            return new BugsnagHandlerExceptionResolver(bugsnag);
        }

        /**
         * Add interceptors to automatically start sessions per request
         * and add request metadata to reports.
         */
        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            RequestMetadataInterceptor requestMetadataInterceptor =
                    new RequestMetadataInterceptor();
            bugsnag.addCallback(requestMetadataInterceptor);

            SessionInterceptor sessionInterceptor = new SessionInterceptor(bugsnag);

            registry.addInterceptor(requestMetadataInterceptor);
            registry.addInterceptor(sessionInterceptor);
        }

        /**
         * Add a callback to assign specified severities for some Spring exceptions.
         */
        @PostConstruct
        void addExceptionClassCallback() {
            bugsnag.addCallback(new ExceptionClassCallback());
        }
    }
}
