package com.bugsnag;

import com.bugsnag.callbacks.Callback;
import com.bugsnag.servlet.BugsnagServletContainerInitializer;
import com.bugsnag.servlet.BugsnagServletRequestListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import javax.servlet.ServletRequestListener;

/**
 * If spring-boot is loaded, add configuration specific to Spring Boot
 */
@Configuration
@Conditional(SpringBootLoadedCondition.class)
class SpringBootConfiguration {

    @Autowired
    private Bugsnag bugsnag;

    /**
     * Add a callback to add the version of Spring Boot used by the application.
     */
    @Bean
    Callback springBootVersionCallback() {
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
     * The {@link BugsnagServletContainerInitializer} does not work for Spring Boot, need to
     * register the {@link BugsnagServletRequestListener} using a Spring Boot
     * {@link ServletListenerRegistrationBean} instead. This adds session tracking and
     * automatic servlet request metadata collection.
     */
    @Bean
    @Conditional(SpringWebMvcLoadedCondition.class)
    ServletListenerRegistrationBean<ServletRequestListener> listenerRegistrationBean() {
        ServletListenerRegistrationBean<ServletRequestListener> srb =
                new ServletListenerRegistrationBean<ServletRequestListener>();
        srb.setListener(new BugsnagServletRequestListener());
        return srb;
    }
}