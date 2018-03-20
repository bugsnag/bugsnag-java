package com.bugsnag.logback;

import com.bugsnag.Report;

import ch.qos.logback.classic.spi.ILoggingEvent;

/** Callback that receives the logging event in addition to the Bugsnag report. */
public interface LogEventAwareCallback {
    /**
     * See {@link com.bugsnag.callbacks.Callback#beforeNotify(Report)}
     *
     * @param report the report to perform changes on.
     * @param event the log event being notified.
     */
    void beforeNotify(Report report, ILoggingEvent event);
}
