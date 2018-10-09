package com.bugsnag.mazerunnerspringboot.scenarios;

import com.bugsnag.mazerunnerspringboot.Scenario;
import com.bugsnag.mazerunnerspringboot.ScheduledTaskService;
import org.springframework.web.client.RestTemplate;

/**
 * Causes an unhandled exception in a scheduled task
 */
public class ScheduledTaskScenario extends Scenario {
    @Override
    public void run() {
        // Enable throwing an exception in the scheduled task
        ScheduledTaskService.setThrowException();

        // Wait for the exception
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
