package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Report;
import com.bugsnag.callbacks.Callback;
import com.bugsnag.mazerunner.Scenario;

/**
 * Sends a handled exception to Bugsnag, which does not include session data.
 */
public class HandledExceptionScenario extends Scenario {
    @Override
    public void run() {
        bugsnag.notify(generateException());
    }
}
