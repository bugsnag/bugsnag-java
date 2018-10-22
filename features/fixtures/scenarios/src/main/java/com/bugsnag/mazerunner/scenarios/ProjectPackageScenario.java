package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

/**
 * Sends a handled exception to Bugsnag, which does not include session data.
 */
public class ProjectPackageScenario extends Scenario {

    public ProjectPackageScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        bugsnag.setProjectPackages("com.bugsnag.mazerunner");
        bugsnag.notify(generateException());
    }
}
