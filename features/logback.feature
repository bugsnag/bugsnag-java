Feature: Using the logback appender

Scenario: Test logback appender
    When I run "LogbackScenario" with logback config "basic_config.xml"
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "LogbackScenario"
    And the event "app.releaseStage" equals "production"
    And the event "app.version" equals "1.0.0"

