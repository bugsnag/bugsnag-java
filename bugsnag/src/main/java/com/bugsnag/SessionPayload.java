package com.bugsnag;

import com.bugsnag.serialization.Expose;

import java.util.Collection;
import java.util.Map;

final class SessionPayload {

    private final Collection<SessionCount> sessionCounts;
    private final Diagnostics diagnostics;
    private final Map<String, Object> device;
    private final Map<String, Object> app;

    SessionPayload(Collection<SessionCount> sessionCounts, Configuration configuration) {
        this.sessionCounts = sessionCounts;
        diagnostics = new Diagnostics(configuration);
        this.device = null;
        this.app = null;
    }

    SessionPayload(Collection<SessionCount> sessionCounts, Map<String, Object> device, Map<String, Object> app) {
        this.sessionCounts = sessionCounts;
        this.diagnostics = null;
        this.device = device;
        this.app = app;
    }

    @Expose
    Notifier getNotifier() {
        return NotifierUtils.getNotifier();
    }

    @Expose
    Map<String, Object> getDevice() {
        return device != null ? device : diagnostics.device;
    }

    @Expose
    Map<String, Object> getApp() {
        return app != null ? app : diagnostics.app;
    }

    @Expose
    Collection<SessionCount> getSessionCounts() {
        return sessionCounts;
    }

}
