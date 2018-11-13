package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;
import com.bugsnag.mazerunnerspringboot.ScheduledTaskService;

/**
 * Causes an unhandled exception in a scheduled task
 */
public class ScheduledTaskScenario extends Scenario {

    public ScheduledTaskScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        // Enable throwing an exception in the scheduled task
        ScheduledTaskService.setThrowException();

        // Wait for the exception
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            // ignore
        }
    }
}
