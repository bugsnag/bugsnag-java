package com.bugsnag;

import com.bugsnag.serialization.Expose;

import java.util.Date;

final class SessionCount {

    private final Date startedAt;
    private final long sessionsStarted;

    SessionCount(Date startedAt, long sessionsStarted) {
        this.startedAt = new Date(startedAt.getTime());
        this.sessionsStarted = sessionsStarted;
    }

    @Expose
    Date getStartedAt() {
        return startedAt;
    }

    @Expose
    long getSessionsStarted() {
        return sessionsStarted;
    }

}
