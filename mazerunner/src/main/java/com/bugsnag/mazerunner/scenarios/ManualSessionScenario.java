package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Report;
import com.bugsnag.callbacks.Callback;
import com.bugsnag.mazerunner.Scenario;

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
