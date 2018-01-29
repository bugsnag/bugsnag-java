package com.bugsnag;

import com.bugsnag.serialization.Expose;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

class Session {

    private final String id;
    private final Date startedAt;
    private final AtomicInteger handledCount;
    private final AtomicInteger unhandledCount;

    Session(String id, Date startedAt) {
        this.id = id;
        this.startedAt = new Date(startedAt.getTime());
        this.handledCount = new AtomicInteger(0);
        this.unhandledCount = new AtomicInteger(0);
    }

    int getHandledCount() {
        return handledCount.get();
    }

    void incrementHandledCount() {
        this.handledCount.incrementAndGet();
    }

    int getUnhandledCount() {
        return unhandledCount.get();
    }

    void incrementUnhandledCount() {
        this.unhandledCount.incrementAndGet();
    }

    String getId() {
        return id;
    }

    Date getStartedAtDate() {
        return new Date(startedAt.getTime());
    }

    @Expose
    String getStartedAt() {
        return DateUtils.toISO8601(startedAt);
    }
}
