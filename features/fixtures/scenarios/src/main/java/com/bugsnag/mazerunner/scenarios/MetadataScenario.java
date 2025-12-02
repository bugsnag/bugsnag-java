package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;
import com.bugsnag.Report;
import com.bugsnag.callbacks.Callback;

/**
 * Sends a handled exception to Bugsnag, which includes custom metadata
 */
public class MetadataScenario extends Scenario {

    public MetadataScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        bugsnag.notify(generateException(), new Callback() {
            @Override
            public boolean onError(Report report) {
                report.addMetadata("Custom", "foo", "Hello World!");
                return true;
            }
        });
    }
}
