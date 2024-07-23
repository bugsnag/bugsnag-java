package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;
import com.bugsnag.Report;
import com.bugsnag.callbacks.Callback;

/**
 * Sends a handled exception to Bugsnag, which contains metadata that should be redacted
 */
public class ManualRedactedKeysScenario extends Scenario {

    public ManualRedactedKeysScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        Pattern pattern = Pattern.compile("[a-zA-Z]{4}");
        bugsnag.setRedactedKeys(Pattern.compile("foo"), pattern);

        bugsnag.notify(generateException(), new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.addToTab("custom", "foo", "hunter2");
                report.addToTab("user", "abcd", "hunter2");
                report.addToTab("custom", "bar", "hunter2");
            }
        });
    }
}
