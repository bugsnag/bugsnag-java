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
            public void onError(Report report) {
                addSpringRuntimeVersion(report.getDevice());
            }
        };
        bugsnag.addCallback(callback);
        return callback;
    }

    @Bean
    OnSession springBootVersionSessionCallback() {
        OnSession onSession = new OnSession() {
            @Override
            public void onSession(SessionPayload payload) {
                addSpringRuntimeVersion(payload.getDevice());
            }
        };
        bugsnag.addOnSession(onSession);
        return onSession;
    }

    private void addSpringRuntimeVersion(Map<String, Object> device) {
        Diagnostics.addDeviceRuntimeVersion(device, "springBoot", SpringBootVersion.getVersion());
    }
}
