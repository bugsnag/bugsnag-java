package com.bugsnag;

import com.bugsnag.callbacks.Callback;
import com.bugsnag.servlet.BugsnagServletRequestListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootVersion;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
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
    Callback springBootVersionErrorCallback() {
        Callback callback = new Callback() {
            @Override
            public void beforeNotify(Report report) {
                appendSpringBootRuntimeVersion(Diagnostics.retrieveRuntimeVersionsMap(report.getDevice()));
            }
        };
        bugsnag.addCallback(callback);
        return callback;
    }

    @Bean
    BeforeSendSession springBootVersionSessionCallback() {
        BeforeSendSession beforeSendSession = new BeforeSendSession() {
            @Override
            public void beforeSendSession(SessionPayload payload) {
                appendSpringBootRuntimeVersion(Diagnostics.retrieveRuntimeVersionsMap(payload.getDevice()));
            }
        };
        bugsnag.addBeforeSendSession(beforeSendSession);
        return beforeSendSession;
    }

    private void appendSpringBootRuntimeVersion(Map<String, Object> runtimeVersions) {
        runtimeVersions.put("springBoot", SpringBootVersion.getVersion());
    }

    /**
     * The {@link com.bugsnag.servlet.BugsnagServletContainerInitializer} does not work for Spring Boot, need to
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
