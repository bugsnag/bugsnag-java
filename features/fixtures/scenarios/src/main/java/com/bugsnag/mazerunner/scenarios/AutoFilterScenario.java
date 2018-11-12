package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;
import com.bugsnag.Report;
import com.bugsnag.callbacks.Callback;

/**
 * Sends a handled exception to Bugsnag, which contains metadata that should be filtered
 */
public class AutoFilterScenario extends Scenario {

    public AutoFilterScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        bugsnag.notify(generateException(), new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.addToTab("user", "password", "hunter2");
                report.addToTab("custom", "password", "hunter2");
                report.addToTab("custom", "foo", "hunter2");
            }
        });
    }
}
