package com.bugsnag.example.logback.cli;

import com.bugsnag.BugsnagAppender;
import com.bugsnag.logback.ExceptionWithCallback;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class ApplicationCommandLineRunner implements CommandLineRunner {

    private static final Logger LOGGER = Logger.getLogger(ApplicationCommandLineRunner.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void run(final String... args) throws Exception {

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

        // Send a handled exception with custom MetaData
        LOGGER.info("Sending a handled exception to Bugsnag with custom MetaData");
        try {
            throw new RuntimeException("Handled exception - custom metadata");
        } catch (RuntimeException e) {
            LOGGER.warn("Something bad happened", new ExceptionWithCallback(e, report -> {
                report.addToTab("report tab", "data key 1", "data value 1");
                report.addToTab("report tab", "data key 2", "data value 2");
            }));
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
        BugsnagAppender.clearThreadMetaData();

        // Exit the spring application
        System.exit(SpringApplication.exit(applicationContext));
    }
}
