package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

/**
 * Sends a handled exception to Bugsnag, which overrides the release stage
 */
public class ReleaseStageScenario extends Scenario {

    public ReleaseStageScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        bugsnag.setReleaseStage("staging");
        bugsnag.notify(generateException());
    }
}
