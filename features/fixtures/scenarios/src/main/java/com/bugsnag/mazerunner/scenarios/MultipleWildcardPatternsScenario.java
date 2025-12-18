package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

/**
 * Tests multiple regex patterns working together.
 */
public class MultipleWildcardPatternsScenario extends Scenario {

    public MultipleWildcardPatternsScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        // Set multiple regex patterns: matching specific packages and classes
        bugsnag.setDiscardClasses(
            "java\\.io\\..*",                           // All java.io exceptions
            "java\\.lang\\.IllegalStateException",      // Exact match
            "java\\.lang\\.Illegal.*"                   // All IllegalXException classes
        );

        // These should all be ignored
        bugsnag.notify(new java.io.IOException("Should be ignored - java\\.io\\..*"));
        bugsnag.notify(new java.io.FileNotFoundException("Should be ignored - java\\.io\\..*"));
        bugsnag.notify(new IllegalStateException("Should be ignored - exact match"));
        bugsnag.notify(new IllegalArgumentException("Should be ignored - java\\.lang\\.Illegal.*"));

        // This should be sent (not matching any pattern)
        bugsnag.notify(new RuntimeException("Should be sent"));
    }
}
