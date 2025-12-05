package com.bugsnag.mazerunner.scenarios;

import com.bugsnag.Bugsnag;

/**
 * Sends a handled exception to Bugsnag with a feature flag set via callback.
 */
public class FeatureFlagCallbackScenario extends Scenario {

    public FeatureFlagCallbackScenario(Bugsnag bugsnag) {
        super(bugsnag);
    }

    @Override
    public void run() {
        bugsnag.notify(generateException(), report -> {
            report.addFeatureFlag("callback_flag", "callback_variant");
            return true;
        });
    }
}
