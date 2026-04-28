package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;
import com.bugsnag.logback.BugsnagMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sends an exception to Bugsnag with custom metadata using the logback appender
 */
public class LogbackMetadataScenario extends Scenario {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogbackMetadataScenario.class);

    public LogbackMetadataScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        LOGGER.warn(new BugsnagMarker(event -> {
            event.addMetadata("user", "foo", "hunter2");
            event.addMetadata("custom", "foo", "hunter2");
            event.addMetadata("custom", "bar", "hunter2");
            return true;
        }), "Error sent to Bugsnag using the logback appender", generateException());
    }
}
