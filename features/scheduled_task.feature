Feature: Reporting Unhandled exceptions in a scheduled task

Scenario: Report an exception from a spring boot scheduled task
    When I run spring boot "ScheduledTaskScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the request used the Spring notifier
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the event "unhandled" is true
    And the event "severity" equals "error"
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "Unhandled exception from ScheduledTaskService"
    And the event "severityReason.type" equals "unhandledExceptionMiddleware"
    And the event "severityReason.attributes.framework" equals "Spring"

Scenario: Report an exception from a plain spring scheduled task
    Given I set environment variable "RUN_SCHEDULED_TASK" to "true"
    And I run the plain spring app
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the request used the Spring notifier
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the event "unhandled" is true
    And the event "severity" equals "error"
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "Unhandled exception from ScheduledTaskService"
    And the event "severityReason.type" equals "unhandledExceptionMiddleware"
    And the event "severityReason.attributes.framework" equals "Spring"


