package com.bugsnag.example.simple;

import java.util.Date;

import com.bugsnag.Client;
import com.bugsnag.Report;
import com.bugsnag.Severity;
import com.bugsnag.callbacks.Callback;

public class ExampleApp {
    public static void main(String[] args) {
        // Create a Bugsnag client
        Client bugsnag = new Client("3fd63394a0ec74ac916fbdf3110ed957");
        // bugsnag.setEndpoint("https://dijfdijfndsfn.com");

        // Set some diagnostic data which will not change during the
        // lifecycle of the application
        bugsnag.setReleaseStage("staging");
        bugsnag.setAppVersion("1.0.1");

        // Create and attach a simple Bugsnag callback.
        // Use Callbacks to send custom diagnostic data which changes during
        // the lifecyle of your application
        bugsnag.addCallback((report) -> {
            report.addToTab("diagnostics", "timestamp", new Date());
            report.addToTab("customer", "name", "acme-inc");
            report.addToTab("customer", "paying", true);
            report.addToTab("customer", "spent", 1234);
        });

        // Send a handled exception to Bugsnag
        try {
            throw new RuntimeException("Handled exception - default severity");
        } catch(RuntimeException e) {
            bugsnag.notify(e);
        }

        // Send a handled exception to Bugsnag with info severity
        try {
            throw new RuntimeException("Handled exception - INFO severity");
        } catch(RuntimeException e) {
            bugsnag.notify(e, Severity.INFO);
        }

        // Send a handled exception with custom MetaData
        try {
            throw new RuntimeException("Handled exception - custom metadata");
        } catch(RuntimeException e) {
            bugsnag.notify(e, (report) -> {
                report.setSeverity(Severity.WARNING);
                report.addToTab("report", "something", "blah");
                report.setContext("blergh");
            });
        }

        // Throw an exception
        throw new RuntimeException("Unhandled exception");
    }
}
