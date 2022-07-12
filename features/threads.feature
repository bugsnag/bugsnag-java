Feature: Sending java thread data

Scenario: Test logback appender with the sendThreads flag set in config
    When I run "LogbackScenario" with logback config "threads_config.xml"
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "LogbackScenario"
    And the error payload field "events.0.threads" is a non-empty array

Scenario: Test Java app with the sendThreads flag set in config
    When I run "ThreadsScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events.0.threads" is a non-empty array

Scenario: Test Spring Boot app with the sendThreads flag set in config
    When I run spring boot "ThreadsScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events.0.threads" is a non-empty array

Scenario: Test Spring app with the sendThreads flag set in config
    When I run plain Spring "ThreadsScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events.0.threads" is a non-empty array

