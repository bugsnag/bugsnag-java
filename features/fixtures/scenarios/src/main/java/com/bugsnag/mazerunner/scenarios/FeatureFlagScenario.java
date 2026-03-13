package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

/**
 * Sends a handled exception to Bugsnag with a feature flag.
 */
public class FeatureFlagScenario extends Scenario {

    public FeatureFlagScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        bugsnag.addFeatureFlag("demo_flag", "variant_a");
        bugsnag.notify(generateException());
    }
}
