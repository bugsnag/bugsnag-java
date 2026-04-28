package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

/**
 * Sends a handled exception to Bugsnag demonstrating clear feature flag.
 */
public class ClearFeatureFlagScenario extends Scenario {

    public ClearFeatureFlagScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        bugsnag.addFeatureFlag("flag_to_clear", "variant");
        bugsnag.addFeatureFlag("flag_to_keep", "variant");
        bugsnag.clearFeatureFlag("flag_to_clear");
        bugsnag.notify(generateException());
    }
}
