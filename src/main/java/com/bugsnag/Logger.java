package com.bugsnag;

class Logger {
    protected static final String LOG_PREFIX = "Bugsnag";

    public void debug(String message) {
        System.out.println(String.format("[%s] DEBUG: %s", LOG_PREFIX, message));
    }

    public void info(String message) {
        System.out.println(String.format("[%s] INFO: %s", LOG_PREFIX, message));
    }

    public void warn(String message) {
        System.err.println(String.format("[%s] WARNING: %s", LOG_PREFIX, message));
    }
}