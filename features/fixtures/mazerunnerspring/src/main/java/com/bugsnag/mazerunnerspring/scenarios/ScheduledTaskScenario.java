package com.bugsnag.mazerunnerspring.scenarios;

import com.bugsnag.mazerunnerspring.Scenario;
import com.bugsnag.mazerunnerspring.ScheduledTaskService;
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
