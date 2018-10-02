package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.mazerunner.Scenario;
import org.apache.log4j.Logger;

/**
 * Sends an exception to Bugsnag using the logback appender
 */
public class LogbackScenario extends Scenario {

    private static final Logger LOGGER = Logger.getLogger(LogbackScenario.class);

    @Override
    public void run() {
        LOGGER.warn("Error sent to Bugsnag using the logback appender", generateException());
    }
}
