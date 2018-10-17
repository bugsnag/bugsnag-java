package com.bugsnag.mazerunner.scenarios;

import org.springframework.web.client.RestTemplate;

/**
 * Causes an unhandled exception in an async method
 */
public class AsyncMethodScenario extends Scenario {
    @Override
    public void run() {

        // Don't report any sessions during this test
        disableSessionDelivery();

        // The rest endpoint will run an async task to throw the exception
        final String uri = "http://localhost:1234/run-async-task";

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
