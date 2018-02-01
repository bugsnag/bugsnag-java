package com.bugsnag;

import com.bugsnag.delivery.Delivery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;

class SessionTracker {

    private final Configuration config;
    private final ThreadLocal<Session> session = new ThreadLocal<Session>();
    private AtomicReference<SessionCount> batchCount = new AtomicReference<SessionCount>();
    private final Collection<SessionCount>
            enqueuedSessionCounts = new ConcurrentLinkedQueue<SessionCount>();

    private final Semaphore flushingRequest = new Semaphore(1);
    private volatile boolean shuttingDown;

    SessionTracker(Configuration configuration) {
        this.config = configuration;
    }

    void startSession(Date date, boolean autoCaptured) {
        if ((!config.shouldAutoCaptureSessions() && autoCaptured)
                || !config.shouldNotifyForReleaseStage()) {
            return;
        }

        // update the current session
        Date roundedStartDate = DateUtils.roundTimeToLatestMinute(date);
        session.set(new Session(UUID.randomUUID().toString(), roundedStartDate));

        // check whether the session count needs to be updated
        updateBatchCountIfNeeded(roundedStartDate);

        // increment the session count
        batchCount.get().incrementSessionsStarted();
    }

    private void updateBatchCountIfNeeded(Date roundedStartDate) {
        boolean isNewBatchPeriod = isNewBatchPeriod(roundedStartDate);

        if (isNewBatchPeriod) {
            SessionCount prev = batchCount.getAndSet(new SessionCount(roundedStartDate));

            if (prev != null && prev.getSessionsStarted() > 0) {
                enqueuedSessionCounts.add(prev);
            }
        }
    }

    private boolean isNewBatchPeriod(Date now) {
        SessionCount val = batchCount.get();
        return val == null || now.after(val.getRoundedDate());
    }

    Session getSession() {
        return session.get();
    }

    void flushSessions(Date now) {
        if (shuttingDown) {
            return;
        }
        updateBatchCountIfNeeded(DateUtils.roundTimeToLatestMinute(now));

        if (!enqueuedSessionCounts.isEmpty() && flushingRequest.tryAcquire(1)) {
            try {
                Collection<SessionCount> requestValues = new ArrayList<SessionCount>();
                requestValues.addAll(enqueuedSessionCounts);

                SessionPayload payload = new SessionPayload(requestValues, config);
                Delivery delivery = config.sessionDelivery;
                delivery.deliver(config.serializer, payload, config.getErrorApiHeaders());
                enqueuedSessionCounts.removeAll(requestValues);
            } finally {
                flushingRequest.release(1);
            }
        }
    }

    void setShuttingDown(boolean shuttingDown) {
        this.shuttingDown = shuttingDown;
    }
}
