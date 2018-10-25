package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;
import com.bugsnag.callbacks.Callback;
import com.bugsnag.Report;

/**
 * Sends an exception to Bugsnag with custom meta data on the thread
 */
public class ThreadMetaDataScenario extends Scenario {

    public ThreadMetaDataScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        // Global callback metadata has lowest precedence
        bugsnag.addCallback(new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.addToTab("Custom", "test", "Global value");
                report.addToTab("Custom", "foo", "Global value to be overwritten");
            }
        });

        // Thread metadata should merge with global metadata and overwrite when duplicate key
        Bugsnag.addThreadMetaData("Custom", "foo", "Thread value");
        Bugsnag.addThreadMetaData("Custom", "bar", "Thread value to be overwritten");

        // Thread metadata on a different thread should not get added
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Bugsnag.addThreadMetaData("Custom", "something", "This should not be on the report");
            }
        });

        try {
            t1.join();
        } catch (InterruptedException ex) {
            // ignore
        }

        // Report-specific metadata should merge with global + thread metadata and overwrite when duplicate key
        bugsnag.notify(generateException(), new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.addToTab("Custom", "bar", "Hello World!");
            }
        });
    }
}
