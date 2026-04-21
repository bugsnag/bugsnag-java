Feature: Feature Flags

Scenario: Test single feature flag on Java app
    When I run "FeatureFlagScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the error payload field "events.0.featureFlags" is an array with 1 elements
    And the error payload field "events.0.featureFlags.0.name" equals "demo_flag"
    And the error payload field "events.0.featureFlags.0.variant" equals "variant_a"

Scenario: Test feature flag set via callback on Java app
    When I run "FeatureFlagCallbackScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the error payload field "events.0.featureFlags" is an array with 1 elements
    And the error payload field "events.0.featureFlags.0.name" equals "callback_flag"
    And the error payload field "events.0.featureFlags.0.variant" equals "callback_variant"

Scenario: Test feature flag override on Java app
    When I run "FeatureFlagOverrideScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the error payload field "events.0.featureFlags" is an array with 1 elements
    And the error payload field "events.0.featureFlags.0.name" equals "override_flag"
    And the error payload field "events.0.featureFlags.0.variant" equals "event_variant"

Scenario: Test multiple feature flags on Java app
    When I run "MultipleFeatureFlagsScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the error payload field "events.0.featureFlags" is an array with 3 elements
    And the error payload field "events.0.featureFlags.0.name" equals "flag_a"
    And the error payload field "events.0.featureFlags.0.variant" equals "variant_1"
    And the error payload field "events.0.featureFlags.1.name" equals "flag_b"
    And the error payload field "events.0.featureFlags.1.variant" is null
    And the error payload field "events.0.featureFlags.2.name" equals "flag_c"
    And the error payload field "events.0.featureFlags.2.variant" equals "variant_3"

Scenario: Test clear feature flag on Java app
    When I run "ClearFeatureFlagScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the error payload field "events.0.featureFlags" is an array with 1 elements
    And the error payload field "events.0.featureFlags.0.name" equals "flag_to_keep"
    And the error payload field "events.0.featureFlags.0.variant" equals "variant"

Scenario: Test single feature flag on Spring Boot app
    When I run spring boot "FeatureFlagScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the error payload field "events.0.featureFlags" is an array with 1 elements
    And the error payload field "events.0.featureFlags.0.name" equals "demo_flag"
    And the error payload field "events.0.featureFlags.0.variant" equals "variant_a"

Scenario: Test multiple feature flags on Spring Boot app
    When I run spring boot "MultipleFeatureFlagsScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the error payload field "events.0.featureFlags" is an array with 3 elements
    And the error payload field "events.0.featureFlags.0.name" equals "flag_a"
    And the error payload field "events.0.featureFlags.0.variant" equals "variant_1"
    And the error payload field "events.0.featureFlags.1.name" equals "flag_b"
    And the error payload field "events.0.featureFlags.1.variant" is null
    And the error payload field "events.0.featureFlags.2.name" equals "flag_c"
    And the error payload field "events.0.featureFlags.2.variant" equals "variant_3"
