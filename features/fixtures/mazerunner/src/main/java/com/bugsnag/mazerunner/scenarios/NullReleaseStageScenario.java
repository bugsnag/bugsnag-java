package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.mazerunner.Scenario;

/**
 * Attempts to send a handled exception to Bugsnag, when the release stage is null.
 */
public class NullReleaseStageScenario extends Scenario {
    @Override
    public void run() {
        bugsnag.setReleaseStage(null);
        bugsnag.setNotifyReleaseStages("dev");
        bugsnag.notify(generateException());
    }
}
