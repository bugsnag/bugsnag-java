package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

/**
 * Attempts to send ignored handled exceptions using regex patterns to Bugsnag,
 * which should not result in any operation.
 */
public class IgnoredExceptionWildcardScenario extends Scenario {

    public IgnoredExceptionWildcardScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        // Use regex pattern to ignore all java.lang exceptions
        bugsnag.setDiscardClasses("java\\.lang\\..*");

        // These should all be ignored due to the regex pattern
        bugsnag.notify(new RuntimeException("Should never appear"));
        bugsnag.notify(new IllegalArgumentException("Should never appear"));
        bugsnag.notify(new IllegalStateException("Should never appear"));

        // This is also ignored due to the regex pattern
        try {
            throw new NullPointerException("Should never appear");
        } catch (Exception e) {
            bugsnag.notify(e);
        }
    }
}
