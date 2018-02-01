package com.bugsnag;

import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Singleton which exposes the {@link SessionTracker} to
 * {@link com.bugsnag.servlet.BugsnagServletRequestListener},
 * intended for internal use
 */
public enum ServletSessionTracker {

    INSTANCE;

    private SessionTracker sessionTracker;

    /**
     * Should be set when bugsnag is initialised
     *
     * @param sessionTracker the session tracker
     */
    void setSessionTracker(SessionTracker sessionTracker) {
        this.sessionTracker = sessionTracker;
    }

    /**
     * Tracks a session from the Servlet API. Intended for internal use only
     */
    public static void trackServletSession() {
        if (INSTANCE.sessionTracker != null) {
            INSTANCE.sessionTracker.startSession(new Date(), true);
        }
    }

}
