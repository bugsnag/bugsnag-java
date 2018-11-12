package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

/**
 * Attempts to send a handled exception to Bugsnag, when the notifyReleaseStages is null.
 */
public class NullNotifyReleaseStageScenario extends Scenario {

    public NullNotifyReleaseStageScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        bugsnag.setReleaseStage("prod");
        bugsnag.notify(generateException());
    }
}
