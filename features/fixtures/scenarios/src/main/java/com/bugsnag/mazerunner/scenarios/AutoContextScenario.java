package com.bugsnag.mazerunner.scenarios;

/**
 * Sends a handled exception to Bugsnag, which includes automatic context.
 */
public class AutoContextScenario extends Scenario {
    @Override
    public void run() {
        bugsnag.notify(generateException());
    }
}
