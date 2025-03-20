package com.bugsnag;

public class BugsnagDesktop extends Bugsnag {

    private BugsnagDesktopConfiguration config;

    private BugsnagDesktopSessionTracker sessionTracker;

    public BugsnagDesktop(String apiKey) {
        this(apiKey, true);
    }

    public BugsnagDesktop(String apiKey, boolean sendUncaughtExceptions) {
        super(apiKey, sendUncaughtExceptions, new BugsnagDesktopConfiguration(apiKey), new BugsnagDesktopSessionTracker());
    }
}

