Feature: Reporting Unhandled exceptions in an async method

Scenario: Report an exception from a spring boot async method
    When I run spring boot "AsyncMethodScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the event "unhandled" is true
    And the event "severity" equals "error"
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "Unhandled exception from Async method"
    And the event "severityReason.type" equals "unhandledExceptionMiddleware"
    And the event "severityReason.attributes.framework" equals "Spring"

Scenario: Notify an exception from a spring boot async method
    When I run spring boot "AsyncNotifyScenario" with the defaults
    Then I wait to receive 2 errors
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "test from before async"
    And the event "metaData.thread.controllerMethod" equals "meta data from controller method"
    And the event "metaData.thread.inAsyncMethod" is null
    Then I discard the oldest error

    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "test from async"
    And the event "metaData.thread.controllerMethod" is null
    And the event "metaData.thread.inAsyncMethod" equals "meta data from async method"

Scenario: Report an exception from a plain spring async method
    Given I run the plain spring app
    When I navigate to the route "/run-async-task" on port "8080"
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the event "unhandled" is true
    And the event "severity" equals "error"
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "Unhandled exception from Async method"
    And the event "severityReason.type" equals "unhandledExceptionMiddleware"
    And the event "severityReason.attributes.framework" equals "Spring"
