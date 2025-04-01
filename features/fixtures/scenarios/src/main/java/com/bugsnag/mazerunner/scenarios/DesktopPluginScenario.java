package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Configuration;
import com.bugsnag.Bugsnag;
import com.bugsnag.BugsnagDesktopPlugin;

public class DesktopPluginScenario extends Scenario {

    public DesktopPluginScenario(Bugsnag bugsnag) {
        super(bugsnag);

        BugsnagDesktopPlugin desktopPlugin = new BugsnagDesktopPlugin(bugsnag.getConfig());
        bugsnag.getConfig().addPlugin(desktopPlugin);
        desktopPlugin.load(bugsnag);
    }

    @Override
    public void run() {
    
        Thread thread = new Thread() {

            @Override
            public void run() {
                bugsnag.startSession();
                throw new RuntimeException("unhandled exception");
            }
        };

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            // ignore
        }
        bugsnag.close();
    }    
}
