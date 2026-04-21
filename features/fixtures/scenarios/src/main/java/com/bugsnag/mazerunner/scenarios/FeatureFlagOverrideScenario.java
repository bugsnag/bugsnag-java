package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

/**
 * Sends a handled exception to Bugsnag demonstrating feature flag override behavior.
 */
public class FeatureFlagOverrideScenario extends Scenario {

    public FeatureFlagOverrideScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        // Add flag at client level
        bugsnag.addFeatureFlag("override_flag", "client_variant");
        
        // Override the flag at event level
        bugsnag.notify(generateException(), report -> {
            report.addFeatureFlag("override_flag", "event_variant");
            return true;
        });
    }
}
