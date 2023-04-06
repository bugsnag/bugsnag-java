package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;
import com.bugsnag.Report;
import com.bugsnag.callbacks.Callback;
import com.bugsnag.mazerunnerspringboot.ScheduledTaskExecutorService;
import java.util.Collection;

public class ScheduledTaskExecutorScenario extends Scenario {

    public ScheduledTaskExecutorScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        // Enable throwing an exception in the scheduled task
        ScheduledTaskExecutorService.setSendException();

        // Wait for the exception
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            // ignore
        }

        final Collection<String> threadnames = ScheduledTaskExecutorService.getThreadNames();
        bugsnag.notify(new RuntimeException("Whoops"), new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.addToTab("executor", "multiThreaded", threadnames.size() > 1);
                report.addToTab("executor", "names", threadnames);
            }
        });
    }
}
