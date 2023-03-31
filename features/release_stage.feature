Feature: Reporting events with different release stages

Scenario: Test logback appender with staging release stage
    When I run "LogbackScenario" with logback config "release_stage_config.xml"
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "LogbackScenario"
    And the event "app.releaseStage" equals "staging"

Scenario: Test Java app with staging release stage
    When I run "ReleaseStageScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the event "app.releaseStage" equals "staging"

Scenario: Test Spring Boot app with staging release stage
    When I run spring boot "ReleaseStageScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the event "app.releaseStage" equals "staging"

Scenario: Test Spring app with staging release stage
    When I run plain Spring "ReleaseStageScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the event "app.releaseStage" equals "staging"
