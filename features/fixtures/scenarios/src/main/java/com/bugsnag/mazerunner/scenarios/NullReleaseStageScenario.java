package com.bugsnag.mazerunner.scenarios;

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
