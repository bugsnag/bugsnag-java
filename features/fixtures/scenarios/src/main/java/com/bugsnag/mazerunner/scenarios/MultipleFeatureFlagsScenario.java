package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

/**
 * Sends a handled exception to Bugsnag demonstrating multiple feature flags.
 */
public class MultipleFeatureFlagsScenario extends Scenario {

    public MultipleFeatureFlagsScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        bugsnag.addFeatureFlag("flag_a", "variant_1");
        bugsnag.addFeatureFlag("flag_b");
        bugsnag.addFeatureFlag("flag_c", "variant_3");
        bugsnag.notify(generateException());
    }
}
