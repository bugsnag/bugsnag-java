package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;
import com.bugsnag.BugsnagEvent;
import com.bugsnag.callbacks.OnErrorCallback;

/**
 * Sends a handled exception to Bugsnag, which contains metadata that should be redacted
 */
public class AutoRedactScenario extends Scenario {

    public AutoRedactScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        bugsnag.notify(generateException(), event -> {
            event.addMetadata("user", "password", "hunter2");
            event.addMetadata("custom", "password", "hunter2");
            event.addMetadata("custom", "foo", "hunter2");
            return true;
        });
    }
}
