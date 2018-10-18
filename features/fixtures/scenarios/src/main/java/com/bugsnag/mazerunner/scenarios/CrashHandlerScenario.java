package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

/**
 * Sends an unhandled exception to Bugsnag, when another exception handler is installed.
 */
public class CrashHandlerScenario extends Scenario {

    public CrashHandlerScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                final Thread.UncaughtExceptionHandler previousHandler =
                        Thread.getDefaultUncaughtExceptionHandler();

                Thread.setDefaultUncaughtExceptionHandler(previousHandler);

                throw new RuntimeException("CrashHandlerScenario");
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
