package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.mazerunner.Scenario;

/**
 * Attempts to deliver a handled exception with no stacktrace.
 */
public class EmptyStacktraceScenario extends Scenario {
    @Override
    public void run() {
        bugsnag.notify(new EmptyException("EmptyStacktraceScenario"));
    }

    private class EmptyException extends Throwable {
        public EmptyException(String message) {
            super(message, null, true, false);
        }
    }
}
