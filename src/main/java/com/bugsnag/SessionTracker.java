package com.bugsnag;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

class SessionTracker {

    private final Configuration config;
    private final ThreadLocal<Session> session = new ThreadLocal<Session>();
    private final ThreadLocal<Calendar> calendarThreadLocal = new ThreadLocal<Calendar>() {
        @Override
        protected Calendar initialValue() {
            return new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        }
    };
    private final Collection<SessionCount> enqueuedSessionCounts = new ConcurrentLinkedQueue<SessionCount>();

    SessionTracker(Configuration configuration) {
        this.config = configuration;
    }

    void startNewSession(Date date, boolean autoCaptured) {
        if ((!config.shouldAutoCaptureSessions()
                && autoCaptured) || !config.shouldNotifyForReleaseStage()) {
            return;
        }

        session.set(new Session(UUID.randomUUID().toString(), date));


        // get current minute in utc
        int minute = getUtcMinute(date);

        // TODO temp flush
        SessionCount count = new SessionCount(session.get().getStartedAtDate());
        count.incrementSessionsStarted();


    }

    Session getSession() {
        return session.get();
    }

    void flushSessions(Date now) {
        if (!enqueuedSessionCounts.isEmpty()) {
            // TODO check empty

            SessionPayload payload = new SessionPayload(enqueuedSessionCounts, config);
            config.sessionDelivery.deliver(config.serializer, payload, config.getErrorApiHeaders());
        }
    }

    private int getUtcMinute(Date date) {
        Calendar calendar = calendarThreadLocal.get();
        calendar.setTime(date);
        return calendar.get(Calendar.MINUTE);
    }

}