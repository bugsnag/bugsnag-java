package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

/**
 * Sends a handled exception to Bugsnag, which overrides the default user via a callback
 */
public class UserCallbackScenario extends Scenario {

    public UserCallbackScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        bugsnag.notify(generateException(), (event) -> {
            event.setUser("Agent Pink", "bob@example.com", "Zebedee");
            return true;
        });
    }
}
