package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;
import com.bugsnag.Report;
import com.bugsnag.callbacks.Callback;
import com.bugsnag.logback.BugsnagMarker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sends an exception to Bugsnag with custom meta data using the logback appender
 */
public class LogbackMetaDataScenario extends Scenario {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogbackMetaDataScenario.class);

    public LogbackMetaDataScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        LOGGER.warn(new BugsnagMarker(new Callback() {
                    @Override
                    public void beforeNotify(Report report) {
                        report.addToTab("user", "foo", "hunter2");
                        report.addToTab("custom", "foo", "hunter2");
                        report.addToTab("custom", "bar", "hunter2");
                    }
                }),"Error sent to Bugsnag using the logback appender", generateException());
    }
}
