package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.mazerunner.Scenario;

/**
 * Sends a handled exception to Bugsnag that contains session information
 */
public class HandledSessionScenario extends Scenario {
    @Override
    public void run() {
        bugsnag.setAppVersion("1.2.3");
        bugsnag.startSession();

        bugsnag.notify(generateException());
    }
}
