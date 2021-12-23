Feature: Reporting Error Context

Scenario: Context automatically set in plain Java app
    When I run "AutoContextScenario" with the defaults
    And I wait for 3 seconds
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the exception "message" equals "AutoContextScenario"
    And the event "context" is null

Scenario: Context manually set in plain Java app
    When I run "ManualContextScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the exception "message" equals "ManualContextScenario"
    And the event "context" equals "FooContext"
