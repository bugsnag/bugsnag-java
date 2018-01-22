package com.bugsnag;

import java.util.Date;

class Session {

    private final String id;
    private final Date startedAt;

    private int handledCount;
    private int unhandledCount;

    Session(String id, Date startedAt) {
        this.id = id;
        this.startedAt = new Date(startedAt.getTime());
    }

    synchronized int getHandledCount() {
        return handledCount;
    }

    synchronized void incrementHandledCount() {
        this.handledCount++;
    }

    synchronized int getUnhandledCount() {
        return unhandledCount;
    }

    synchronized void incrementUnhandledCount() {
        this.unhandledCount++;
    }

    String getId() {
        return id;
    }

    Date getStartedAt() {
        return new Date(startedAt.getTime());
    }
}
