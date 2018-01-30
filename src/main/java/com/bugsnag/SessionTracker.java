package com.bugsnag;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

class SessionTracker {

    private final Configuration config;
    private final ThreadLocal<Session> session = new ThreadLocal<Session>();
    private final AtomicLong sessionCount = new AtomicLong();

    SessionTracker(Configuration configuration) {
        this.config = configuration;
    }

    void startNewSession(Date date, boolean autoCaptured) {
        if ((!config.shouldAutoCaptureSessions() && autoCaptured) || !config.shouldNotifyForReleaseStage()) {
            return;
        }

        // TODO store date against count and deliver sessions
        sessionCount.incrementAndGet();
        session.set(new Session(UUID.randomUUID().toString(), date));

        // TODO temp flush
        List<SessionCount> sessionCounts = Collections.singletonList(new SessionCount(session.get().getStartedAtDate(), 1));
        SessionPayload payload = new SessionPayload(sessionCounts, config);
        config.sessionDelivery.deliver(config.serializer, payload, config.getErrorApiHeaders());
    }

    Session getSession() {
        return session.get();
    }

}