package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Report;
import com.bugsnag.callbacks.Callback;
import com.bugsnag.mazerunner.Scenario;

/**
 * Sends a handled exception to Bugsnag, which includes automatic context.
 */
public class AutoContextScenario extends Scenario {
    @Override
    public void run() {
        bugsnag.notify(generateException());
    }
}
