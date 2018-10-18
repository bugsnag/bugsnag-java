Feature: Sending java thread data

Scenario: Test logback appender with the sendThreads flag set in config
    When I run "LogbackScenario" with logback config "threads_config.xml"
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "LogbackScenario"
    And the payload field "events.0.threads" is a non-empty array

Scenario: Test Java app with the sendThreads flag set in config
    When I run "ThreadsScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the payload field "events.0.threads" is a non-empty array

Scenario: Test Spring Boot app with the sendThreads flag set in config
    When I run spring boot "ThreadsScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the payload field "events.0.threads" is a non-empty array

Scenario: Test Spring app with the sendThreads flag set in config
    When I run plain Spring "ThreadsScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the payload field "events.0.threads" is a non-empty array

