package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;
import org.springframework.web.client.RestTemplate;

/**
 * Causes an unhandled exception in the rest controller
 */
public class RestControllerScenario extends Scenario {

    public RestControllerScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        // Don't report any sessions during this test
        disableSessionDelivery();

        final String uri = "http://localhost:1234/send-unhandled-exception";

        try {
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri, String.class);
        } catch (Exception ex) {
            // ignore
        }
    }
}
