Feature: Reporting Error Context

Scenario: Context automatically set in plain Java app
    When I run "AutoContextScenario" with the defaults
    And I wait for 3 seconds
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the exception "message" equals "AutoContextScenario"
    And the event "context" is null

Scenario: Context manually set in plain Java app
    When I run "ManualContextScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the exception "message" equals "ManualContextScenario"
    And the event "context" equals "FooContext"
