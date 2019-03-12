package com.bugsnag;

import com.bugsnag.delivery.Delivery;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

class SessionTracker {

    private final Configuration config;
    private final ThreadLocal<Session> session = new ThreadLocal<Session>();
    private final AtomicReference<SessionCount> batchCount = new AtomicReference<SessionCount>();
    private final Collection<SessionCount>
            enqueuedSessionCounts = new ConcurrentLinkedQueue<SessionCount>();

    private final Semaphore flushingRequest = new Semaphore(1);
    private final AtomicBoolean shuttingDown = new AtomicBoolean();

    SessionTracker(Configuration configuration) {
        this.config = configuration;
    }

    void startSession(Date date, boolean autoCaptured) {
        if ((!config.shouldAutoCaptureSessions() && autoCaptured)
                || !config.shouldNotifyForReleaseStage()
                || shuttingDown.get()) {
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
        if (isNewBatchPeriod(roundedStartDate)) {
            synchronized (batchCount) {
                // check again in case already updated in another thread
                if (isNewBatchPeriod(roundedStartDate)) {
                    SessionCount newCount = new SessionCount(roundedStartDate);
                    SessionCount prevCount = batchCount.getAndSet(newCount);

                    if (prevCount != null && prevCount.getSessionsStarted() > 0) {
                        enqueuedSessionCounts.add(prevCount);
                    }
                }
            }
        }
    }

    private boolean isNewBatchPeriod(Date roundedStartDate) {
        SessionCount currentCount = batchCount.get();
        return currentCount == null || roundedStartDate.after(currentCount.getRoundedDate());
    }

    Session getSession() {
        return session.get();
    }

    SessionCount getBatchCount() {
        return batchCount.get();
    }

    void flushSessions(Date now) {
        if (shuttingDown.get()) {
            return;
        }
        sendSessions(now);
    }

    private void sendSessions(Date now) {
        updateBatchCountIfNeeded(DateUtils.roundTimeToLatestMinute(now));

        if (!enqueuedSessionCounts.isEmpty() && flushingRequest.tryAcquire(1)) {
            try {
                Collection<SessionCount> requestValues
                        = new ArrayList<SessionCount>(enqueuedSessionCounts);
                SessionPayload payload = new SessionPayload(requestValues, config);
                Delivery delivery = config.sessionDelivery;
                delivery.deliver(config.serializer, payload, config.getSessionApiHeaders());
                enqueuedSessionCounts.removeAll(requestValues);
            } finally {
                flushingRequest.release(1);
            }
        }
    }

    void shutdown() {
        if (shuttingDown.compareAndSet(false, true)) {
            sendSessions(new Date(Long.MAX_VALUE)); // flush all remaining sessions
        }
    }
}
