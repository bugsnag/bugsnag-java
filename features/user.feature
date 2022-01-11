Feature: Reporting User Information

Scenario: Override user details in callback in Java app
    When I run "UserCallbackScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the exception "message" equals "UserCallbackScenario"
    And the event "user.id" equals "Agent Pink"
    And the event "user.email" equals "bob@example.com"
    And the event "user.name" equals "Zebedee"

Scenario: Override user details in callback in Spring Boot app
    When I run spring boot "UserCallbackScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the exception "message" equals "UserCallbackScenario"
    And the event "user.id" equals "Agent Pink"
    And the event "user.email" equals "bob@example.com"
    And the event "user.name" equals "Zebedee"

Scenario: Override user details in callback in Spring app
    When I run plain Spring "UserCallbackScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the exception "message" equals "UserCallbackScenario"
    And the event "user.id" equals "Agent Pink"
    And the event "user.email" equals "bob@example.com"
    And the event "user.name" equals "Zebedee"
