package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.mazerunner.Scenario;

/**
 * Sends an unhandled exception to Bugsnag that contains session information
 */
public class UnhandledSessionScenario extends Scenario {
    @Override
    public void run() {
        bugsnag.setAppVersion("1.2.3");

        Thread t1 = new Thread(new Runnable() {
            public void run()
            {
                final Thread.UncaughtExceptionHandler previousHandler = Thread.getDefaultUncaughtExceptionHandler();

                Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread t, Throwable e) {
                        previousHandler.uncaughtException(t, e);
                    }
                });

                bugsnag.startSession();
                throw new RuntimeException("UnhandledSessionScenario");
            }});
        t1.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
