Feature: Reporting Unhandled exceptions in a rest controller

Scenario: Report an exception from a spring boot rest controller
    When I run spring boot "RestControllerScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the event "unhandled" is true
    And the event "severity" equals "error"
    And the event "context" equals "GET /send-unhandled-exception"
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "Unhandled exception from TestRestController"
    And the event "metaData.request.url" ends with "/send-unhandled-exception"
    And the event "severityReason.type" equals "unhandledExceptionMiddleware"
    And the event "severityReason.attributes.framework" equals "Spring"

Scenario: Report an exception from a plain spring rest controller
    Given I run the plain spring app
    When I navigate to the route "/send-unhandled-exception" on port "8080"
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the event "unhandled" is true
    And the event "severity" equals "error"
    And the event "context" equals "GET /send-unhandled-exception"
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "Unhandled exception from TestRestController"
    And the event "metaData.request.url" ends with "/send-unhandled-exception"
    And the event "severityReason.type" equals "unhandledExceptionMiddleware"
    And the event "severityReason.attributes.framework" equals "Spring"
