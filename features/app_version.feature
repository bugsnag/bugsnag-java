Feature: Reporting app version

Scenario: Test Java handled Exception
    When I run "AppVersionScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "AppVersionScenario"
    And the event "app.version" equals "1.2.3.abc"

Scenario: Test Spring Boot handled Exception
    When I run spring boot "AppVersionScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "AppVersionScenario"
    And the event "app.version" equals "1.2.3.abc"

Scenario: Test plain Spring handled Exception
    When I run plain Spring "AppVersionScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "AppVersionScenario"
    And the event "app.version" equals "1.2.3.abc"

Scenario: Test logback appender with app version 1.2.3
    When I run "LogbackScenario" with logback config "app_version_config.xml"
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "LogbackScenario"
    And the event "app.version" equals "1.2.3"