package com.bugsnag;

import com.bugsnag.callbacks.Callback;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootVersion;
import org.springframework.context.annotation.Bean;

import java.util.Map;

public class SpringBootConfiguration {
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
                addSpringRuntimeVersion(report.getDevice());
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
                addSpringRuntimeVersion(payload.getDevice());
            }
        };
        bugsnag.addBeforeSendSession(beforeSendSession);
        return beforeSendSession;
    }

    private void addSpringRuntimeVersion(Map<String, Object> device) {
        Diagnostics.addDeviceRuntimeVersion(device, "springBoot", SpringBootVersion.getVersion());
    }
}
