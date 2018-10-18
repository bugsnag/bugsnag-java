package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

/**
 * Attempts to deliver a handled exception with no stacktrace.
 */
public class EmptyStacktraceScenario extends Scenario {

    public EmptyStacktraceScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        bugsnag.notify(new EmptyException("EmptyStacktraceScenario"));
    }

    private static class EmptyException extends Throwable {
        private static final long serialVersionUID = 6420625703155845924L;

        EmptyException(String message) {
            super(message, null, true, false);
        }
    }
}
