package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.mazerunner.Scenario;

/**
 * Attempts to send a handled exception to Bugsnag, when the release stage is set.
 */
public class InsideReleaseStageScenario extends Scenario {
    @Override
    public void run() {
        bugsnag.setReleaseStage("prod");
        bugsnag.setNotifyReleaseStages("prod");
        bugsnag.notify(generateException());
    }
}
