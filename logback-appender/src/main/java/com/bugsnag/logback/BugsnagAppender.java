package com.bugsnag.logback;

import com.bugsnag.Bugsnag;
import com.bugsnag.Report;
import com.bugsnag.Severity;
import com.bugsnag.callbacks.Callback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Bugsnag log appender that reports logs supplied with a Throwable to Bugsnag.
 */
public abstract class BugsnagAppender extends AppenderBase<LoggingEvent> {

    private static Logger LOGGER = LoggerFactory.getLogger(BugsnagAppender.class);
    Bugsnag bugsnag;

    protected BugsnagAppender() {
        super();
        bugsnag = initBugsnag();
    }

    /**
     * Initialize the Bugsnag client to be used by the logging appender.
     * @return the Bugsnag client.
     */
    protected abstract Bugsnag initBugsnag();

    @Override
    protected void append(LoggingEvent event) {
        if (bugsnag == null) {
            LOGGER.debug("Cannot notify Bugsnag - bugsnag is null");
            return;
        }

        Throwable throwable;
        Severity severity;
        if (event.getLevel().levelInt <= Level.INFO.levelInt) {
            severity = Severity.INFO;
        } else if (event.getLevel().levelInt <= Level.WARN.levelInt) {
            severity = Severity.WARNING;
        } else {
            severity = Severity.ERROR;
        }

        if (event.getThrowableProxy() != null) {
            // Throwable provided
            throwable = ((ThrowableProxy)event.getThrowableProxy()).getThrowable();

            if (throwable == null) {
                LOGGER.debug("Cannot notify Bugsnag - throwable is null");
            } else {
                final String logMessage = event.getMessage();
                bugsnag.notify(throwable, severity, new Callback() {
                    @Override
                    public void beforeNotify(Report report) {
                        report.addToTab("Log Message", "Message", logMessage);
                    }
                });
            }
        }
    }
}
