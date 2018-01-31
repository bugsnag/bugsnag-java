package com.bugsnag;

import com.bugsnag.serialization.Expose;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

final class SessionCount {

    private final String startedAt;
    private final AtomicLong sessionsStarted = new AtomicLong();

    SessionCount(Date startedAt) {
        this.startedAt = DateUtils.toIso8601(startedAt);
    }

    void incrementSessionsStarted() {
        sessionsStarted.incrementAndGet();
    }

    @Expose
    String getStartedAt() {
        return startedAt;
    }

    @Expose
    long getSessionsStarted() {
        return sessionsStarted.get();
    }

}
