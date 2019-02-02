package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;
import com.bugsnag.Report;
import com.bugsnag.callbacks.Callback;

/**
 * Sends a handled exception to Bugsnag, which includes custom metadata
 */
public class MetaDataScenario extends Scenario {

    public MetaDataScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        bugsnag.notify(generateException(), new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.addToTab("Custom", "foo", "Hello World!");
            }
        });
    }
}
