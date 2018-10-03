Feature: Reporting app version

Scenario: Test logback appender with app type 'testAppType'
    When I run "LogbackScenario" with logback config "app_type_config.xml"
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "LogbackScenario"
    And the event "app.type" equals "testAppType"
