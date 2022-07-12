Feature: Reporting app version

Scenario: Test Java handled Exception
    When I run "AppVersionScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "AppVersionScenario"
    And the event "app.version" equals "1.2.3.abc"

Scenario: Test Spring Boot handled Exception
    When I run spring boot "AppVersionScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "AppVersionScenario"
    And the event "app.version" equals "1.2.3.abc"

Scenario: Test plain Spring handled Exception
    When I run plain Spring "AppVersionScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "AppVersionScenario"
    And the event "app.version" equals "1.2.3.abc"

Scenario: Test logback appender with app version 1.2.3
    When I run "LogbackScenario" with logback config "app_version_config.xml"
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "LogbackScenario"
    And the event "app.version" equals "1.2.3"