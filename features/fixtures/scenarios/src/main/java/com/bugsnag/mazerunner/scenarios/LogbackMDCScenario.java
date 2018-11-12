package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * Sends an exception to Bugsnag with MDC meta data using the logback appender
 */
public class LogbackMDCScenario extends Scenario {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogbackMDCScenario.class);

    public LogbackMDCScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        MDC.put("foo", "hunter2");
        MDC.put("bar", "hunter2");

        LOGGER.warn("Error sent to Bugsnag using the logback appender", generateException());
    }
}
