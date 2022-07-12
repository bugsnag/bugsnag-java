Feature: Reporting app version

Scenario: Test logback appender with app type 'testAppType'
    When I run "LogbackScenario" with logback config "app_type_config.xml"
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "LogbackScenario"
    And the event "app.type" equals "testAppType"

Scenario: Test Java app with app type 'testAppType'
    When I run "AppTypeScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the exception "message" equals "AppTypeScenario"
    And the event "app.type" equals "testAppType"

Scenario: Test Spring Boot app with app type 'testAppType'
    When I run spring boot "AppTypeScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the exception "message" equals "AppTypeScenario"
    And the event "app.type" equals "testAppType"

Scenario: Test Spring app with app type 'testAppType'
    When I run the plain spring app
    And I wait for 20 seconds
    And I navigate to the route "/run-scenario/AppTypeScenario" on port "8080"
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the exception "message" equals "AppTypeScenario"
    And the event "app.type" equals "testAppType"
