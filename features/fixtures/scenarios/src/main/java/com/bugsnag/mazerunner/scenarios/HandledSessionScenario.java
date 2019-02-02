package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;
import com.bugsnag.delivery.Delivery;
import com.bugsnag.serialization.Serializer;

import java.util.Map;

/**
 * Sends a handled exception to Bugsnag that contains session information
 */
public class HandledSessionScenario extends Scenario {

    public HandledSessionScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

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

        bugsnag.startSession();
        bugsnag.notify(generateException());
    }
}
