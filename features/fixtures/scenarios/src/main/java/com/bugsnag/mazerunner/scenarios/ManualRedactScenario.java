package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;
import com.bugsnag.Report;
import com.bugsnag.callbacks.Callback;

/**
 * Sends a handled exception to Bugsnag, which contains metadata that should be redacted
 */
public class ManualRedactScenario extends Scenario {

    public ManualRedactScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {

        bugsnag.setRedactedKeys("foo");

        bugsnag.notify(generateException(), new Callback() {
            @Override
            public boolean onError(Report report) {
                report.addToTab("user", "foo", "hunter2");
                report.addToTab("custom", "foo", "hunter2");
                report.addToTab("custom", "bar", "hunter2");
                return true;
            }
        });
    }
}
