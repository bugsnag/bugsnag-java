package com.bugsnag;

import com.bugsnag.serialization.Expose;

import java.util.Date;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

class Session {

    private final Semaphore incrementRequest = new Semaphore(1);

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

    private Session(String id, Date startedAt, int handledCount, int unhandledCount) {
        this.id = id;
        this.startedAt = startedAt;
        this.handledCount = new AtomicInteger(handledCount);
        this.unhandledCount = new AtomicInteger(unhandledCount);
    }

    int getHandledCount() {
        return handledCount.get();
    }

    void incrementHandledCount() {
        this.handledCount.incrementAndGet();
    }

    Session incrementHandledCountAndClone() throws InterruptedException {
        try {
            incrementRequest.acquire();
            incrementHandledCount();
            return cloneSession();
        } finally {
            incrementRequest.release();
        }
    }

    int getUnhandledCount() {
        return unhandledCount.get();
    }

    void incrementUnhandledCount() {
        this.unhandledCount.incrementAndGet();
    }

    Session incrementUnhandledCountAndClone() throws InterruptedException {
        try {
            incrementRequest.acquire();
            incrementUnhandledCount();
            return cloneSession();
        } finally {
            incrementRequest.release();
        }
    }

    private Session cloneSession() {
        return new Session(id, startedAt, handledCount.get(), unhandledCount.get());
    }

    String getId() {
        return id;
    }

    Date getStartedAtDate() {
        return new Date(startedAt.getTime());
    }

    @Expose
    String getStartedAt() {
        return DateUtils.toIso8601(startedAt);
    }
}
