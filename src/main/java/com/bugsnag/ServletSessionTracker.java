package com.bugsnag;

import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Singleton which exposes the {@link SessionTracker} to {@link com.bugsnag.servlet.BugsnagServletRequestListener},
 * intended for internal use
 */
public enum ServletSessionTracker {

    INSTANCE;

    private SessionTracker sessionTracker;

    /**
     * Should be set when
     * @param sessionTracker the session tracker
     */
    void setSessionTracker(SessionTracker sessionTracker) {
        LoggerFactory.getLogger("foo").warn("Set tracker!");
        this.sessionTracker = sessionTracker;
    }

    public static void trackServletSession() {
        if (INSTANCE.sessionTracker != null) {
            INSTANCE.sessionTracker.startSession(new Date(), true);
            LoggerFactory.getLogger("foo").warn("Tracking auto session!");
        } else {
            LoggerFactory.getLogger("foo").warn("Null session!");
        }
    }

}
