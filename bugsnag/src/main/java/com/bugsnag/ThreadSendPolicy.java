package com.bugsnag;

/**
 * Controls whether we should capture and serialize the state of all threads at the time of an error.
 */
public enum ThreadSendPolicy {
    /**
     * Threads should be captured for all events. This is often not useful for Java web apps, since
     * there could be thousands of active threads depending on your environment.
     */
    ALWAYS,

    /**
     * Threads should be captured for unhandled events only.
     */
    UNHANDLED_ONLY,

    /**
     * Threads should never be captured.
     */
    NEVER
}
