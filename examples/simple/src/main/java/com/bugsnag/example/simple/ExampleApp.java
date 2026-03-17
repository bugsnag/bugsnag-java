package com.bugsnag.example.simple;

import com.bugsnag.Bugsnag;
import com.bugsnag.BugsnagEvent;
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
            public boolean onError(BugsnagEvent event) {
                event.addMetadata("diagnostics", "timestamp", new Date());
                event.addMetadata("customer", "name", "acme-inc");
                event.addMetadata("customer", "paying", true);
                event.addMetadata("customer", "spent", 1234);
                event.setUserName("User Name");
                event.setUserEmail("user@example.com");
                event.setUserId("12345");
                return true;
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

        // Send a handled exception with custom Metadata
        try {
            throw new RuntimeException("Handled exception - custom metadata");
        } catch (RuntimeException e) {
            bugsnag.notify(e, new Callback() {
                @Override
                public boolean onError(BugsnagEvent event) {
                    event.setSeverity(Severity.WARNING);
                    event.addMetadata("report", "something", "that happened");
                    event.setContext("the context");
                    return true;
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
