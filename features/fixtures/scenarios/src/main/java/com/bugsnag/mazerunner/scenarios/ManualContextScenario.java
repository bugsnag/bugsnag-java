package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

/**
 * Sends a handled exception to Bugsnag, which includes manual context.
 */
public class ManualContextScenario extends Scenario {

    public ManualContextScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        bugsnag.notify(generateException(), event -> {
            event.setContext("FooContext");
            return true;
        });
    }
}
