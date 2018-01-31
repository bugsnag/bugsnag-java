package com.bugsnag;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SessionTracker {

    private final Configuration config;
    private final ThreadLocal<Session> session = new ThreadLocal<Session>();

    SessionTracker(Configuration configuration) {
        this.config = configuration;
    }

    public void startNewSession(Date date, boolean autoCaptured) {
        if ((!config.shouldAutoCaptureSessions() && autoCaptured) || !config.shouldNotifyForReleaseStage()) {
            return;
        }

        // TODO store date against count and deliver sessions
        session.set(new Session(UUID.randomUUID().toString(), date));

        // TODO temp flush
        SessionCount count = new SessionCount(session.get().getStartedAtDate());
        count.incrementSessionsStarted();

        List<SessionCount> sessionCounts = Collections.singletonList(count);
        SessionPayload payload = new SessionPayload(sessionCounts, config);
        config.sessionDelivery.deliver(config.serializer, payload, config.getErrorApiHeaders());
    }

    Session getSession() {
        return session.get();
    }

}