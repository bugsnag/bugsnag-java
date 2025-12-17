package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

/**
 * Attempts to send ignored handled exceptions using wildcard patterns to Bugsnag, 
 * which should not result in any operation.
 */
public class IgnoredExceptionWildcardScenario extends Scenario {

    public IgnoredExceptionWildcardScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        // Use wildcard pattern to ignore all RuntimeException and its subclasses
        bugsnag.setDiscardClasses("java.lang.*");

        // These should all be ignored due to the wildcard pattern
        bugsnag.notify(new RuntimeException("Should never appear"));
        bugsnag.notify(new IllegalArgumentException("Should never appear"));
        bugsnag.notify(new IllegalStateException("Should never appear"));
        
        // This should also be sent but will be ignored due to pattern
        try {
            throw new NullPointerException("Should never appear");
        } catch (Exception e) {
            bugsnag.notify(e);
        }
    }
}
