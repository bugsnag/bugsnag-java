package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;
import com.bugsnag.Report;
import com.bugsnag.callbacks.Callback;

/**
 * Sends a handled exception to Bugsnag, which overrides the default user via a callback
 */
public class ThreadsScenario extends Scenario {

    public ThreadsScenario(Bugsnag bugsnag) {
        super(bugsnag);
        bugsnag.setSendThreads(true);
    }

    @Override
    public void run() {
        bugsnag.notify(generateException());
    }
}
