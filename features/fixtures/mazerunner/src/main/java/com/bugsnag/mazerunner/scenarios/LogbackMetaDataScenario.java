package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Report;
import com.bugsnag.callbacks.Callback;
import com.bugsnag.logback.ExceptionWithCallback;
import com.bugsnag.mazerunner.Scenario;
import org.apache.log4j.Logger;

/**
 * Sends an exception to Bugsnag with custom meta data using the logback appender
 */
public class LogbackMetaDataScenario extends Scenario {

    private static final Logger LOGGER = Logger.getLogger(LogbackMetaDataScenario.class);

    @Override
    public void run() {
        LOGGER.warn("Error sent to Bugsnag using the logback appender", new ExceptionWithCallback(generateException(), new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.addToTab("user", "foo", "hunter2");
                report.addToTab("custom", "foo", "hunter2");
                report.addToTab("custom", "bar", "hunter2");
            }
        }));
    }
}
