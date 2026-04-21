package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

/**
 * Sends a handled exception to Bugsnag, which includes custom metadata
 */
public class MetadataScenario extends Scenario {

    public MetadataScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        bugsnag.notify(generateException(), event -> {
            event.addMetadata("Custom", "foo", "Hello World!");
            return true;
        });
    }
}
