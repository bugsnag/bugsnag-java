package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

/**
 * Sends an unhandled exception to Bugsnag with custom metadata on the thread
 */
public class UnhandledThreadMetadataScenario extends Scenario {

    public UnhandledThreadMetadataScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        // Global callback metadata has lowest precedence
        bugsnag.addOnError((event) -> {
            event.addMetadata("Custom", "test", "Global value");
            event.addMetadata("Custom", "foo", "Global value to be overwritten");
            return true;
        });

        // Thread metadata on a different thread should not get added
        Thread t1 = new Thread(() ->
                Bugsnag.addThreadMetadata(
                        "Custom",
                        "something",
                        "This should not be on the report"
                )
        );

        t1.start();

        try {
            t1.join();
        } catch (InterruptedException ex) {
            // ignore
        }

        Thread t2 = new Thread(() -> {
            // Thread metadata should merge with global metadata and overwrite when duplicate key
            Bugsnag.addThreadMetadata("Custom", "foo", "Thread value 1");
            Bugsnag.addThreadMetadata("Custom", "bar", "Thread value 2");
            throw new RuntimeException("UnhandledThreadMetadataScenario");
        });
        t2.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            // ignore
        }
    }
}
