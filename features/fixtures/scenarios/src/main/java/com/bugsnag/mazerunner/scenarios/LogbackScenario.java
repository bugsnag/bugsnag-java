package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sends an exception to Bugsnag using the logback appender
 */
public class LogbackScenario extends Scenario {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogbackScenario.class);

    public LogbackScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        LOGGER.warn("Error sent to Bugsnag using the logback appender", generateException());
    }
}
