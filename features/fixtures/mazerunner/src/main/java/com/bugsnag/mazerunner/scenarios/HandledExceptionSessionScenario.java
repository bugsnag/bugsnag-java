package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.mazerunner.Scenario;

/**
 * Sends a handled exception to Bugsnag, which includes session data.
 */
public class HandledExceptionSessionScenario extends Scenario {
    @Override
    public void run() {
        disableSessionDelivery();
        bugsnag.startSession();
        bugsnag.notify(generateException());
    }
}
