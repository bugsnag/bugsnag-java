Feature: Empty Stacktrace reported

Scenario: Exceptions with empty stacktraces are sent in plain Java app
    When I run "EmptyStacktraceScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "com.bugsnag.mazerunner.scenarios.EmptyStacktraceScenario$EmptyException"
    And the error payload field "events.0.exceptions.0.stacktrace" is null

Scenario: Exceptions with empty stacktraces are sent in Spring Boot app
    When I run spring boot "EmptyStacktraceScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "com.bugsnag.mazerunner.scenarios.EmptyStacktraceScenario$EmptyException"
    And the error payload field "events.0.exceptions.0.stacktrace" is null

Scenario: Exceptions with empty stacktraces are sent in Spring app
    When I run plain Spring "EmptyStacktraceScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "com.bugsnag.mazerunner.scenarios.EmptyStacktraceScenario$EmptyException"
    And the error payload field "events.0.exceptions.0.stacktrace" is null
