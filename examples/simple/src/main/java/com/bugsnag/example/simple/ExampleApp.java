package com.bugsnag.example.simple;

import com.bugsnag.Bugsnag;
import com.bugsnag.Report;
import com.bugsnag.Severity;
import com.bugsnag.callbacks.Callback;

import java.util.Date;

public class ExampleApp {
    public static void main(String[] args) throws InterruptedException {
        // Create a Bugsnag client
        Bugsnag bugsnag = new Bugsnag("YOUR-API-KEY");

        // Set some diagnostic data which will not change during the
        // lifecycle of the application
        bugsnag.setReleaseStage("staging");
        bugsnag.setAppVersion("1.0.1");

        // Create and attach a simple Bugsnag callback.
        // Use Callbacks to send custom diagnostic data which changes during
        // the lifecyle of your application
        bugsnag.addCallback(new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.addToTab("diagnostics", "timestamp", new Date());
                report.addToTab("customer", "name", "acme-inc");
                report.addToTab("customer", "paying", true);
                report.addToTab("customer", "spent", 1234);
                report.setUserName("User Name");
                report.setUserEmail("user@example.com");
                report.setUserId("12345");
            }
        });

        // Send a handled exception to Bugsnag
        try {
            throw new RuntimeException("Handled exception - default severity");
        } catch (RuntimeException e) {
            bugsnag.notify(e);
        }

        // Send a handled exception to Bugsnag with info severity
        try {
            throw new RuntimeException("Handled exception - INFO severity");
        } catch (RuntimeException e) {
            bugsnag.notify(e, Severity.INFO);
        }

        // Send a handled exception with custom MetaData
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
        Thread thread = new Thread() {
            @Override
            public void run() {
                throw new RuntimeException("Unhandled exception");
            }
        };

        thread.start();

        // Wait for unhandled exception thread to finish before exiting
        thread.join();
    }
}
