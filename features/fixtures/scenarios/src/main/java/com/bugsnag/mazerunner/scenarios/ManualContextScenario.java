package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Report;
import com.bugsnag.callbacks.Callback;

/**
 * Sends a handled exception to Bugsnag, which includes manual context.
 */
public class ManualContextScenario extends Scenario {
    @Override
    public void run() {
        bugsnag.notify(generateException(), new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.setContext("FooContext");
            }
        });
    }
}
