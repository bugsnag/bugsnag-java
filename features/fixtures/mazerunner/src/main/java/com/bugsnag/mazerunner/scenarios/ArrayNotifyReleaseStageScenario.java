package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.mazerunner.Scenario;

/**
 * Attempts to send a handled exception to Bugsnag, when the notifyReleaseStages is an array.
 */
public class ArrayNotifyReleaseStageScenario extends Scenario {
    @Override
    public void run() {
        bugsnag.setReleaseStage("prod");
        bugsnag.setNotifyReleaseStages("dev", "prod");
        bugsnag.notify(generateException());
    }
}
