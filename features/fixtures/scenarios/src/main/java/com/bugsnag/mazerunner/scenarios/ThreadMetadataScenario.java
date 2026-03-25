package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

/**
 * Sends an exception to Bugsnag with custom metadata on the thread
 */
public class ThreadMetadataScenario extends Scenario {

    public ThreadMetadataScenario(Bugsnag bugsnag) {
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

        // Thread metadata should merge with global metadata and overwrite when duplicate key
        Bugsnag.addThreadMetadata("Custom", "foo", "Thread value");
        Bugsnag.addThreadMetadata("Custom", "bar", "Thread value to be overwritten");

        // Thread metadata on a different thread should not get added
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Bugsnag.addThreadMetadata("Custom", "something", "This should not be on the report");
            }
        });

        t1.start();

        try {
            t1.join();
        } catch (InterruptedException ex) {
            // ignore
        }

        // Report-specific metadata should merge with global + thread metadata and overwrite when duplicate key
        bugsnag.notify(generateException(), (event) -> {
            event.addMetadata("Custom", "bar", "Hello World!");
            return true;
        });
    }
}
