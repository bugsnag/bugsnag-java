package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

/**
 * Sends a handled exception to Bugsnag, which overrides the app type
 */
public class AppTypeScenario extends Scenario {

    public AppTypeScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        bugsnag.setAppType("testAppType");
        bugsnag.notify(generateException());
    }
}
