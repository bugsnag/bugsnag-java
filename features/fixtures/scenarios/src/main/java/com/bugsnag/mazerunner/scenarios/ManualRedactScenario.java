package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

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

        bugsnag.notify(generateException(), event -> {
            event.addMetadata("user", "foo", "hunter2");
            event.addMetadata("custom", "foo", "hunter2");
            event.addMetadata("custom", "bar", "hunter2");
            return true;
        });
    }
}
