package com.bugsnag;

import com.bugsnag.callbacks.Callback;

import com.bugsnag.servlet.BugsnagServletRequestListener;

import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.SpringVersion;

import javax.annotation.PostConstruct;
import javax.servlet.ServletRequestListener;

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
     * If spring-boot is loaded, add configuration specific to Spring Boot
     */
    @Configuration
    @Conditional(SpringBootLoadedCondition.class)
    public class SpringBootConfiguration {
        /**
         * Add a callback to add the version of Spring Boot used by the application.
         */
        @Bean
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
         * Add a callback to add the version of Spring Boot used by the application.
         */
        @Bean
        public ServletListenerRegistrationBean<ServletRequestListener> listenerRegistrationBean() {
            ServletListenerRegistrationBean<ServletRequestListener> srb =
                    new ServletListenerRegistrationBean<ServletRequestListener>();
            srb.setListener(new BugsnagServletRequestListener());
            return srb;
        }
    }

    /**
     * If spring-webmvc is loaded, add configuration for reporting unhandled exceptions
     * and session tracking.
     */
    @Configuration
    @Conditional(SpringWebMvcLoadedCondition.class)
    public class SpringWebMvcConfiguration {

        /**
         * Register an exception resolver to send unhandled reports to Bugsnag
         * for uncaught exceptions thrown from request handlers.
         */
        @Bean
        public BugsnagHandlerExceptionResolver bugsnagHandlerExceptionResolver() {
            return new BugsnagHandlerExceptionResolver(bugsnag);
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
