package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;
import com.bugsnag.mazerunner.Scenario;
import org.springframework.util.StringUtils;

/**
 * Throws an unhandled exception in a thread, when uncaught exceptions are disabled
 * in Bugsnag. Nothing should be reported
 */
public class DisableAutoNotifyScenario extends Scenario {

    public DisableAutoNotifyScenario() {
        super(false);
    }

    @Override
    public void run() {

        Thread t1 = new Thread(new Runnable() {
            public void run()
            {
                throw new RuntimeException("Should never appear");
            }});
        t1.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
