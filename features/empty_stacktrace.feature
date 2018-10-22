Feature: Empty Stacktrace reported

Scenario: Exceptions with empty stacktraces are sent in plain Java app
    When I run "EmptyStacktraceScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the exception "errorClass" equals "com.bugsnag.mazerunner.scenarios.EmptyStacktraceScenario$EmptyException"
    And the payload field "events.0.exceptions.0.stacktrace" is null

Scenario: Exceptions with empty stacktraces are sent in Spring Boot app
    When I run spring boot "EmptyStacktraceScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the exception "errorClass" equals "com.bugsnag.mazerunner.scenarios.EmptyStacktraceScenario$EmptyException"
    And the payload field "events.0.exceptions.0.stacktrace" is null

Scenario: Exceptions with empty stacktraces are sent in Spring app
    When I run plain Spring "EmptyStacktraceScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the exception "errorClass" equals "com.bugsnag.mazerunner.scenarios.EmptyStacktraceScenario$EmptyException"
    And the payload field "events.0.exceptions.0.stacktrace" is null
