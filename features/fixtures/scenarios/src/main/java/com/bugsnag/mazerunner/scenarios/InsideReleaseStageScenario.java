package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

/**
 * Attempts to send a handled exception to Bugsnag, when the release stage is set.
 */
public class InsideReleaseStageScenario extends Scenario {

    public InsideReleaseStageScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        bugsnag.setReleaseStage("prod");
        bugsnag.setNotifyReleaseStages("prod");
        bugsnag.notify(generateException());
    }
}
