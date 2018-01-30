package com.bugsnag;

import java.util.Date;
import java.util.UUID;

class SessionTracker {

    private final Configuration configuration;
    private final ThreadLocal<Session> session = new ThreadLocal<Session>();

    SessionTracker(Configuration configuration) {
        this.configuration = configuration;
    }

    void startNewSession(Date date, boolean autoCaptured) {
        if ((!configuration.shouldAutoCaptureSessions() && autoCaptured) || !configuration.shouldNotifyForReleaseStage()) {
            return;
        }

        Session newSession = new Session(UUID.randomUUID().toString(), date);


        // TODO increment count/report!
        session.set(newSession);
    }

    Session getSession() {
        return session.get();
    }

}