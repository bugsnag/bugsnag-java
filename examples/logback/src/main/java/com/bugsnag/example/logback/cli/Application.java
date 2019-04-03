package com.bugsnag.example.logback.cli;

import com.bugsnag.Bugsnag;
import com.bugsnag.BugsnagAppender;
import com.bugsnag.logback.BugsnagMarker;

import ch.qos.logback.core.Appender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {

        ch.qos.logback.classic.Logger rootLogger = (ch.qos.logback.classic.Logger)LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        Appender appender = rootLogger.getAppender("BUGSNAG");
        if (appender instanceof BugsnagAppender) {
            // Set some global meta data (added to each report)
            ((BugsnagAppender) appender).getClient().addCallback((report) -> {
                report.addToTab("diagnostics", "timestamp", new Date());
                report.addToTab("customer", "name", "acme-inc");
                report.addToTab("customer", "paying", true);
                report.addToTab("customer", "spent", 1234);
                report.setUserName("User Name");
                report.setUserEmail("user@example.com");
                report.setUserId("12345");
            });
        }

        // Add meta data that will be added to all reports on the current thread
        Bugsnag.addThreadMetaData("thread tab", "thread key 1", "thread value 1");

        // Send a handled exception to Bugsnag
        LOGGER.info("Sending a handled exception to Bugsnag");
        try {
            throw new RuntimeException("Handled exception - default severity");
        } catch (RuntimeException e) {
            LOGGER.warn(e.getMessage(), e);
        }

        // Send a handled exception to Bugsnag with info severity
        LOGGER.info("Sending a handled exception to Bugsnag with INFO severity");
        try {
            throw new RuntimeException("Handled exception - INFO severity");
        } catch (RuntimeException e) {
            LOGGER.info(e.getMessage(), e);
        }

        // Send a handled exception with custom MetaData
        LOGGER.info("Sending a handled exception to Bugsnag with custom MetaData");
        try {
            throw new RuntimeException("Handled exception - custom metadata");
        } catch (RuntimeException e) {
            LOGGER.warn(new BugsnagMarker((report) -> {
                report.addToTab("report tab", "data key 1", "data value 1");
                report.addToTab("report tab", "data key 2", "data value 2");
            }), "Something bad happened", e);
        }

        // Test an unhandled exception from a different thread as shutdown hooks
        // won't be called if executed from this thread
        LOGGER.info("Sending an unhandled exception to Bugsnag");
        Thread thread = new Thread() {
            @Override
            public void run() {
                throw new RuntimeException("Unhandled exception");
            }
        };

        thread.start();

        // Wait for unhandled exception thread to finish before exiting
        thread.join();

        // Remove the thread meta data so it won't be added to future reports on this thread
        Bugsnag.clearThreadMetaData();

        // Exit the application
        System.exit(0);
    }
}
