package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;
import org.springframework.web.client.RestTemplate;

/**
 * Causes an unhandled exception in the rest controller
 */
public class AutoSessionScenario extends Scenario {

    public AutoSessionScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        bugsnag.setAutoCaptureSessions(true);

        final String uri = "http://localhost:1234/add-session";

        try {
            RestTemplate restTemplate = new RestTemplate();
            String result = restTemplate.getForObject(uri, String.class);
            LOGGER.info("Completed auto session request: " + result);
            Thread.sleep(2000);
        } catch (Exception ex) {
            LOGGER.error("Failed to complete request", ex);
        }

        flushAllSessions();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
}
