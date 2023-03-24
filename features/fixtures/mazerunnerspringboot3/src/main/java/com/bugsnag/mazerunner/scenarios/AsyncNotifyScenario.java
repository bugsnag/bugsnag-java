package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;
import org.springframework.web.client.RestTemplate;

/**
 * Notifies from an async method
 */
public class AsyncNotifyScenario extends Scenario {

    public AsyncNotifyScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {

        // Don't report any sessions during this test
        disableSessionDelivery();

        // The rest endpoint will run an async task to throw the exception
        final String uri = "http://localhost:1234/notify-async-task";

        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForObject(uri, String.class);

            // Wait for the async task to complete
            Thread.sleep(2000);
        } catch (Exception ex) {
            // ignore
        }
    }
}
