Feature: Reporting with other exception handlers installed

Scenario: Other uncaught exception handler installed in Java app
    When I run "CrashHandlerScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "CrashHandlerScenario"

Scenario: Other uncaught exception handler installed in Spring Boot app
    When I run spring boot "CrashHandlerScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "CrashHandlerScenario"

Scenario: Other uncaught exception handler installed in Spring app
    When I run plain Spring "CrashHandlerScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "CrashHandlerScenario"