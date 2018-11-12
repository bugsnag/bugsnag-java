package com.bugsnag;

import com.bugsnag.serialization.Expose;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

final class SessionCount {

    private final String startedAt;
    private final AtomicLong sessionsStarted = new AtomicLong();
    private final Date roundedDate;

    SessionCount(Date startedAt) {
        roundedDate = DateUtils.roundTimeToLatestMinute(startedAt);
        this.startedAt = DateUtils.toIso8601(roundedDate);
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

    Date getRoundedDate() {
        return roundedDate;
    }
}
