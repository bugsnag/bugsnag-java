package com.bugsnag.mazerunner.scenarios;

import org.springframework.web.client.RestTemplate;

/**
 * Causes an unhandled exception in the rest controller
 */
public class AutoSessionScenario extends Scenario {
    @Override
    public void run() {
        bugsnag.setAutoCaptureSessions(true);

        final String uri = "http://localhost:1234/add-session";

        try {
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri, String.class);

            Thread.sleep(2000);
        } catch (Exception ex) {
            // ignore
        }

        flushAllSessions();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
