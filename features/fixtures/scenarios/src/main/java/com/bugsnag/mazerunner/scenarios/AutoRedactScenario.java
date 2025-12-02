package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;
import com.bugsnag.Report;
import com.bugsnag.callbacks.Callback;

/**
 * Sends a handled exception to Bugsnag, which contains metadata that should be redacted
 */
public class AutoRedactScenario extends Scenario {

    public AutoRedactScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        bugsnag.notify(generateException(), new Callback() {
            @Override
            public boolean onError(Report report) {
                report.addMetadata("user", "password", "hunter2");
                report.addMetadata("custom", "password", "hunter2");
                report.addMetadata("custom", "foo", "hunter2");
                return true;
            }
        });
    }
}
