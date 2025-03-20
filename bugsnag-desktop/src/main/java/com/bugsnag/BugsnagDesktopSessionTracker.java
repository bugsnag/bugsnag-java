package com.bugsnag;

import java.util.Date;
import java.util.UUID;

public class BugsnagDesktopSessionTracker {
    private BugsnagDesktopSession currentSession;

    public synchronized void startSession(Date startedAt, String deviceId)
    {
        System.out.println("Starting session: " + deviceId);
        currentSession = new BugsnagDesktopSession(UUID.randomUUID().toString(), startedAt, deviceId);
    }

    public synchronized void incrementHandled() { 
        if (currentSession != null) {
            currentSession.incrementHandledCount();
        }
    }

    public synchronized void incrementUnhandled() {
        if (currentSession != null) {
            currentSession.incrementUnhandledCount();
        }
    }

    public synchronized BugsnagDesktopSession getCurrentSession() {
        return currentSession;
    }

}