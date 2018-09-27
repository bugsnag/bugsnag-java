package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.delivery.Delivery;
import com.bugsnag.mazerunner.Scenario;
import com.bugsnag.serialization.Serializer;

import java.util.Map;

/**
 * Sends an unhandled exception to Bugsnag that contains session information
 */
public class UnhandledSessionScenario extends Scenario {
    @Override
    public void run() {
        bugsnag.setAppVersion("1.2.3");

        // Stop sessions being sent to Bugsnag for this case
        bugsnag.setSessionDelivery(new Delivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {

            }

            @Override
            public void close() {

            }
        });

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
