package com.bugsnag.mazerunner.scenarios;

/**
 * Sends a handled exception to Bugsnag, which overrides the app version
 */
public class AppVersionScenario extends Scenario {
    @Override
    public void run() {
        bugsnag.setAppVersion("1.2.3.abc");
        bugsnag.notify(generateException());
    }
}
