package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

import static com.bugsnag.TestHooks.disableSendUncaughtExceptions;

/**
 * Throws an unhandled exception in a thread, when uncaught exceptions are disabled
 * in Bugsnag. Nothing should be reported
 */
public class DisableAutoNotifyScenario extends Scenario {

    public DisableAutoNotifyScenario(Bugsnag bugsnag) {
        super(bugsnag);
        disableSendUncaughtExceptions(bugsnag);
    }

    @Override
    public void run() {

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                throw new RuntimeException("Should never appear");
            }
        });
        t1.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            // ignore
        }
    }
}
