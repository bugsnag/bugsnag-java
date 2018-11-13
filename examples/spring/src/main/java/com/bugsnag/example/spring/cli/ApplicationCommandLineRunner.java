package com.bugsnag.example.spring.cli;

import com.bugsnag.Bugsnag;
import com.bugsnag.Report;
import com.bugsnag.Severity;
import com.bugsnag.callbacks.Callback;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ApplicationCommandLineRunner implements CommandLineRunner {

    private static final Logger LOGGER = Logger.getLogger(ApplicationCommandLineRunner.class);

    // Inject the bugsnag notifier bean defined in Config.java
    @Autowired
    private Bugsnag bugsnag;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void run(final String... args) throws Exception {
        // Send a handled exception to Bugsnag
        LOGGER.info("Sending a handled exception to Bugsnag");
        try {
            throw new RuntimeException("Handled exception - default severity");
        } catch (RuntimeException e) {
            bugsnag.notify(e);
        }

        // Send a handled exception to Bugsnag with info severity
        LOGGER.info("Sending a handled exception to Bugsnag with INFO severity");
        try {
            throw new RuntimeException("Handled exception - INFO severity");
        } catch (RuntimeException e) {
            bugsnag.notify(e, Severity.INFO);
        }

        // Send a handled exception with custom MetaData
        LOGGER.info("Sending a handled exception to Bugsnag with custom MetaData");
        try {
            throw new RuntimeException("Handled exception - custom metadata");
        } catch (RuntimeException e) {
            bugsnag.notify(e, new Callback() {
                @Override
                public void beforeNotify(Report report) {
                    report.setSeverity(Severity.WARNING);
                    report.addToTab("report", "something", "that happened");
                    report.setContext("the context");
                }
            });
        }

        // Test an unhanded exception from a different thread as shutdown hooks
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

        // Exit the spring application
        System.exit(SpringApplication.exit(applicationContext));
    }
}
