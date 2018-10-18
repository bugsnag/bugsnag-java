package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

/**
 * Sends a manual session payload to Bugsnag.
 */
public class ManualSessionScenario extends Scenario {

    public ManualSessionScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        bugsnag.setAppVersion("1.2.3");
        bugsnag.startSession();

        flushAllSessions();
    }
}
