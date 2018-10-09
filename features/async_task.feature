Feature: Reporting Unhandled exceptions in an async task

Scenario: Report an exception from a scheduled task
    When I run spring boot "AsyncTaskScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the event "severity" equals "error"
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "Unhandled exception from AsyncTask"


