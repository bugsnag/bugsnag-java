package com.bugsnag;

import com.bugsnag.callbacks.OnErrorCallback;

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
    OnErrorCallback springBootVersionErrorCallback() {
        OnErrorCallback callback = new OnErrorCallback() {
            @Override
            public boolean onError(BugsnagEvent report) {
                addSpringRuntimeVersion(report.getDevice());
                return true;
            }
        };
        bugsnag.addOnError(callback);
        return callback;
    }

    @Bean
    OnSession springBootVersionSessionCallback() {
        OnSession onSession = new OnSession() {
            @Override
            public boolean onSession(SessionPayload payload) {
                addSpringRuntimeVersion(payload.getDevice());
                return true;
            }
        };
        bugsnag.addOnSession(onSession);
        return onSession;
    }

    private void addSpringRuntimeVersion(Map<String, Object> device) {
        Diagnostics.addDeviceRuntimeVersion(device, "springBoot", SpringBootVersion.getVersion());
    }
}
