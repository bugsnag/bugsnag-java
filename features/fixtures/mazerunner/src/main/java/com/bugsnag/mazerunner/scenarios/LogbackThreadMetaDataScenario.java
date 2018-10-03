package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.BugsnagAppender;
import com.bugsnag.Report;
import com.bugsnag.callbacks.Callback;
import com.bugsnag.logback.ExceptionWithCallback;
import com.bugsnag.mazerunner.Scenario;
import org.apache.log4j.Logger;

/**
 * Sends an exception to Bugsnag with custom meta data using the logback appender
 */
public class LogbackThreadMetaDataScenario extends Scenario {

    private static final Logger LOGGER = Logger.getLogger(LogbackThreadMetaDataScenario.class);

    @Override
    public void run() {
        BugsnagAppender.addThreadMetaData("thread", "foo", "threadvalue1");
        BugsnagAppender.addThreadMetaData("thread", "bar", "threadvalue2");

        LOGGER.warn("Error sent to Bugsnag using the logback appender", generateException());
    }
}
