package com.bugsnag.logback;

import com.bugsnag.Report;
import com.bugsnag.Severity;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;

/** Abstract implementation that may be used as base implementation of callbacks. */
public abstract class AbstractLogEventAwareCallback implements LogEventAwareCallback {

    @Override
    public void beforeNotify(Report report, ILoggingEvent event) {
        report.setSeverity(defineSeverity(event));
    }

    private Severity defineSeverity(ILoggingEvent event) {
        if (event.getLevel().equals(Level.ERROR)) {
            return Severity.ERROR;
        } else if (event.getLevel().equals(Level.WARN)) {
            return Severity.WARNING;
        }
        return Severity.INFO;
    }
}
