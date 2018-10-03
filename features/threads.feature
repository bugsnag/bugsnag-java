Feature: Sending java thread data

Scenario: Test logback appender with the sendThreads flag set in config
    When I run "LogbackScenario" with logback config "threads_config.xml"
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "LogbackScenario"
    And the event "threads" is not null

