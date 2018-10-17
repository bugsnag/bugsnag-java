package com.bugsnag.mazerunner.scenarios;

/**
 * Sends a manual session payload to Bugsnag.
 */
public class ManualSessionScenario extends Scenario {
    @Override
    public void run() {
        bugsnag.setAppVersion("1.2.3");
        bugsnag.startSession();

        flushAllSessions();
    }
}
