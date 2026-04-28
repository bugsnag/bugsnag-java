package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;
import com.bugsnag.BugsnagAppender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Sends an exception to Bugsnag with custom metadata using the logback appender
 */
public class LogbackThreadMetadataScenario extends Scenario {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(LogbackThreadMetadataScenario.class);

    public LogbackThreadMetadataScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        Bugsnag.addThreadMetadata("thread", "foo", "threadvalue1");
        Bugsnag.addThreadMetadata("thread", "bar", "threadvalue2");

        // Thread metadata on a different thread should not get added
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Bugsnag.addThreadMetadata("Custom", "something", "This should not be on the report");
            }
        });

        t1.start();

        try {
            t1.join();
        } catch (InterruptedException ex) {
            // ignore
        }

        LOGGER.warn("Error sent to Bugsnag using the logback appender", generateException());
    }
}
