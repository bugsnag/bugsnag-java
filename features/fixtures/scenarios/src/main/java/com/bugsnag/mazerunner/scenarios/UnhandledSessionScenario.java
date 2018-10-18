package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;
import com.bugsnag.delivery.Delivery;
import com.bugsnag.serialization.Serializer;

import java.util.Map;

/**
 * Sends an unhandled exception to Bugsnag that contains session information
 */
public class UnhandledSessionScenario extends Scenario {

    public UnhandledSessionScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        bugsnag.setAppVersion("1.2.3");

        // Stop sessions being sent to Bugsnag for this case
        bugsnag.setSessionDelivery(new Delivery() {
            @Override
            public void deliver(Serializer serializer,
                                Object object,
                                Map<String, String> headers) {
            }

            @Override
            public void close() {

            }
        });

        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                final Thread.UncaughtExceptionHandler previousHandler =
                        Thread.getDefaultUncaughtExceptionHandler();

                Thread.setDefaultUncaughtExceptionHandler(previousHandler);

                bugsnag.startSession();
                throw new RuntimeException("UnhandledSessionScenario");
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
