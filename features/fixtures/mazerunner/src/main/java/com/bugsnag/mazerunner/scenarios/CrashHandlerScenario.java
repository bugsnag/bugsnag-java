package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.mazerunner.Scenario;

/**
 * Sends an unhandled exception to Bugsnag, when another exception handler is installed.
 */
public class CrashHandlerScenario extends Scenario {
    @Override
    public void run() {

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

                throw new RuntimeException("CrashHandlerScenario");
            }});
        t1.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
