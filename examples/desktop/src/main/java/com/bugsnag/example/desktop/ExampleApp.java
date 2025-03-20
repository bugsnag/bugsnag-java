package com.bugsnag.example.desktop;

import com.bugsnag.BugsnagDesktopPlugin;
import com.bugsnag.Bugsnag;

public class ExampleApp {
    public static void main(String[] args) throws InterruptedException {
        // Create a Bugsnag client
        Bugsnag bugsnag = new Bugsnag("YOUR-API-KEY");
        BugsnagDesktopPlugin plugin = new BugsnagDesktopPlugin(bugsnag);
        plugin.initialize();

        bugsnag.setAutoCaptureSessions(true);

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

        bugsnag.close();
    }
}
