package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;
import com.bugsnag.Report;
import com.bugsnag.callbacks.Callback;

/**
 * Sends a handled exception to Bugsnag, which overrides the default user via a callback
 */
public class UserCallbackScenario extends Scenario {

    public UserCallbackScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        bugsnag.notify(generateException(), new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.setUser("Agent Pink", "bob@example.com", "Zebedee");
            }
        });
    }
}
