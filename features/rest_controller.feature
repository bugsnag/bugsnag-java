Feature: Reporting Unhandled exceptions in a rest controller

Scenario: Report an exception from a spring boot rest controller
    When I run spring boot "RestControllerScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the event "severity" equals "error"
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "Unhandled exception from TestRestController"

Scenario: Report an exception from a plain spring rest controller
    Given I run the plain spring app
    When I navigate to the route "/mazerunnerplainspring/send-unhandled-exception" on port "1235"
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the request used the Spring notifier
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the event "unhandled" is true
    And the event "severity" equals "error"
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "Unhandled exception from TestRestController"
    And the event "metaData.request.url" ends with "/mazerunnerplainspring/send-unhandled-exception"
    And the event "severityReason.type" equals "unhandledExceptionMiddleware"
    And the event "severityReason.attributes.framework" equals "Spring"
