package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.mazerunner.Scenario;

/**
 * Attempts to send a handled exception to Bugsnag, when the notifyReleaseStages is null.
 */
public class NullNotifyReleaseStageScenario extends Scenario {
    @Override
    public void run() {
        bugsnag.setReleaseStage("prod");
        bugsnag.notify(generateException());
    }
}
