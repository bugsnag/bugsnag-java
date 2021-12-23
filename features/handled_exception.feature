Feature: Reporting handled Exceptions

Scenario: Test Java app handled Exception
    When I run "HandledExceptionScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "HandledExceptionScenario"

Scenario: Test Spring Boot handled Exception
    When I run spring boot "HandledExceptionScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "HandledExceptionScenario"

Scenario: Test Spring handled Exception
    When I run plain Spring "HandledExceptionScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "HandledExceptionScenario"
