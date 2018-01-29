package com.bugsnag;

import com.bugsnag.serialization.Expose;

import java.util.Date;

final class SessionCount {

    private final String startedAt;
    private final long sessionsStarted;

    SessionCount(Date startedAt, long sessionsStarted) {
        this.startedAt = DateUtils.toISO8601(startedAt);
        this.sessionsStarted = sessionsStarted;
    }

    @Expose
    String getStartedAt() {
        return startedAt;
    }

    @Expose
    long getSessionsStarted() {
        return sessionsStarted;
    }

}
