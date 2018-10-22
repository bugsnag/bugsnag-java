package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

/**
 * Attempts to send an ignored handled exception to Bugsnag, which should not result
 * in any operation.
 */
public class IgnoredExceptionScenario extends Scenario {

    public IgnoredExceptionScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {

        bugsnag.setIgnoreClasses("java.lang.RuntimeException");

        bugsnag.notify(new RuntimeException("Should never appear"));
    }
}
