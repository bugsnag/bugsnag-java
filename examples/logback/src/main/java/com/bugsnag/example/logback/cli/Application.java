package com.bugsnag.example.logback.cli;

import com.bugsnag.BugsnagAppender;
import com.bugsnag.logback.BugsnagMarker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.util.Date;

public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        // Set some global meta data (added to each report)
        BugsnagAppender.getInstance().addCallback((report) -> {
            report.addToTab("diagnostics", "timestamp", new Date());
            report.addToTab("customer", "name", "acme-inc");
            report.addToTab("customer", "paying", true);
            report.addToTab("customer", "spent", 1234);
            report.setUserName("User Name");
            report.setUserEmail("user@example.com");
            report.setUserId("12345");
        });

        // Add meta data that will be added to all reports on the current thread
        BugsnagAppender.addThreadMetaData("thread tab", "thread key 1", "thread value 1");

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

        LOGGER.error(new BugsnagMarker((report) -> {
            report.addToTab("report", "key1", "value added in callback");
        }), "Something bad happend", new RuntimeException("Something bad happened"));

        // Remove the thread meta data so it won't be added to future reports on this thread
        BugsnagAppender.clearThreadMetaData();

        // Exit the application
        System.exit(0);
    }
}
