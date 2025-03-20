package com.bugsnag.example.desktop;

import com.bugsnag.BugsnagDesktop;
import com.bugsnag.Report;
import com.bugsnag.Severity;
import com.bugsnag.callbacks.Callback;

import java.util.Date;

public class ExampleApp {
    public static void main(String[] args) throws InterruptedException {
        // Create a Bugsnag client
        BugsnagDesktop bugsnag = new Bugsnag("85dc29dec3cb3f172de8b7a213934868");

        // Set some diagnostic data which will not change during the
        // lifecycle of the application
        bugsnag.setReleaseStage("staging");
        bugsnag.setAppVersion("1.0.1");

        // Send a handled exception to Bugsnag
        try {
            throw new RuntimeException("Handled exception - default severity");
        } catch (RuntimeException e) {
            bugsnag.notify(e);
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
