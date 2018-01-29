package com.bugsnag;

import com.bugsnag.callbacks.DeviceCallback;
import com.bugsnag.serialization.Expose;

import java.util.*;

final class SessionPayload {

    private final Collection<SessionCount> sessionCounts = new ArrayList<SessionCount>();;
    private final Configuration configuration;

    SessionPayload(Collection<SessionCount> sessionCounts, Configuration configuration) {
        this.sessionCounts.addAll(sessionCounts);
        this.configuration = configuration;
    }

    @Expose
    Notifier getNotifier() {
        return new Notifier();
    }

    @Expose
    Map<String, Object> getDevice() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("hostname", DeviceCallback.getHostnameValue());
        map.put("osName", System.getProperty("os.name"));
        map.put("osVersion", System.getProperty("os.version"));
        return map;
    }

    @Expose
    Map<String, Object> getApp() {
        Map<String, Object> map = new HashMap<String, Object>();

        if (configuration.releaseStage != null) {
            map.put("releaseStage", configuration.releaseStage);
        }
        if (configuration.appVersion != null) {
            map.put("appVersion", configuration.appVersion);
        }
        return map;
    }

    @Expose
    Collection<SessionCount> getSessionCounts() {
        return sessionCounts;
    }

}
