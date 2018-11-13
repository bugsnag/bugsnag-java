package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

/**
 * Attempts to send a handled exception to Bugsnag, when the notifyReleaseStages is an array.
 */
public class ArrayNotifyReleaseStageScenario extends Scenario {

    public ArrayNotifyReleaseStageScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        bugsnag.setReleaseStage("prod");
        bugsnag.setNotifyReleaseStages("dev", "prod");
        bugsnag.notify(generateException());
    }
}
