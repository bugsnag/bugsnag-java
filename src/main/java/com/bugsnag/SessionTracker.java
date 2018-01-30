package com.bugsnag;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

class SessionTracker {

    private final Configuration configuration;
    private final ThreadLocal<Session> session = new ThreadLocal<Session>();

    private final AtomicLong sessionCount = new AtomicLong();

    SessionTracker(Configuration configuration) {
        this.configuration = configuration;
    }

    void startNewSession(Date date, boolean autoCaptured) {
        if ((!configuration.shouldAutoCaptureSessions() && autoCaptured) || !configuration.shouldNotifyForReleaseStage()) {
            return;
        }

        // TODO store date against count and deliver sessions
        sessionCount.incrementAndGet();
        session.set(new Session(UUID.randomUUID().toString(), date));
    }

    Session getSession() {
        return session.get();
    }

}