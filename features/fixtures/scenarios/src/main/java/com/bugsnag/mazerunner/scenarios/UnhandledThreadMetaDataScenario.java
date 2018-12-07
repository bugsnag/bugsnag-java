package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;
import com.bugsnag.callbacks.Callback;
import com.bugsnag.Report;

/**
 * Sends an unhandled exception to Bugsnag with custom meta data on the thread
 */
public class UnhandledThreadMetaDataScenario extends Scenario {

    public UnhandledThreadMetaDataScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        // Global callback metadata should overwrite thread meta data
        bugsnag.addCallback(new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.addToTab("Custom", "foo", "Global value");
            }
        });

        // Thread metadata on a different thread should not get added
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                Bugsnag.addThreadMetaData("Custom", "something", "This should not be on the report");
            }
        });
        t1.start();

        try {
            t1.join();
        } catch (InterruptedException ex) {
            // ignore
        }

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                // Thread metadata should be overwritten by global callback
                Bugsnag.addThreadMetaData("Custom", "test", "Thread value");
                Bugsnag.addThreadMetaData("Custom", "foo", "Thread value to be overwritten");
                throw new RuntimeException("UnhandledThreadMetaDataScenario");
            }
        });
        t2.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            // ignore
        }
    }
}
