package com.bugsnag;

import com.bugsnag.serialization.Expose;

import java.util.Collection;
import java.util.Map;

final class SessionPayload {

    private final Collection<SessionCount> sessionCounts;
    private final Diagnostics diagnostics;

    SessionPayload(Collection<SessionCount> sessionCounts, Configuration configuration) {
        this.sessionCounts = sessionCounts;
        diagnostics = new Diagnostics(configuration);
    }

    @Expose
    Notifier getNotifier() {
        return new Notifier();
    }

    @Expose
    Map<String, Object> getDevice() {
        return diagnostics.device;
    }

    @Expose
    Map<String, Object> getApp() {
        return diagnostics.app;
    }

    @Expose
    Collection<SessionCount> getSessionCounts() {
        return sessionCounts;
    }

}
