package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

import java.util.regex.Pattern;

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

        bugsnag.setDiscardClasses(Pattern.compile("java.lang.RuntimeException"));

        bugsnag.notify(new RuntimeException("Should never appear"));
    }
}
