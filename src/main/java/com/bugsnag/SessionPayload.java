package com.bugsnag;

import com.bugsnag.serialization.Expose;

import java.util.Collection;
import java.util.Collections;

class SessionPayload {

//    private Device device;
//
//    private App app;
//
//    private List<SessionCount> sessionCounts;

    @Expose
    public Notifier getNotifier() {
        return new Notifier();
    }

//    @Expose
//    public Device getDevice() {
//        return device;
//    }
//
//    @Expose
//    public App getApp() {
//        return app;
//    }
//
    @Expose
    public Collection<SessionCount> getSessionCounts() {
        return Collections.emptyList();
    }

}
