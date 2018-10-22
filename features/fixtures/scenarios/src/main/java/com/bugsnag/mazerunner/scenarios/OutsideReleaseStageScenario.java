package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

/**
 * Attempts to send a handled exception to Bugsnag, when the release stage is not included.
 * This should result in no operation.
 */
public class OutsideReleaseStageScenario extends Scenario {

    public OutsideReleaseStageScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        bugsnag.setReleaseStage("prod");
        bugsnag.setNotifyReleaseStages("dev");
        bugsnag.notify(generateException());
    }
}
