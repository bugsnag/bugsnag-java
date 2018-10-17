package com.bugsnag.mazerunner.scenarios;

/**
 * Sends a handled exception to Bugsnag, which does not include session data.
 */
public class HandledExceptionScenario extends Scenario {
    @Override
    public void run() {
        bugsnag.notify(generateException());
    }
}
