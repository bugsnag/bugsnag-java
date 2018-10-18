Feature: Reporting events with different release stages

Scenario: Test logback appender with staging release stage
    When I run "LogbackScenario" with logback config "release_stage_config.xml"
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "LogbackScenario"
    And the event "app.releaseStage" equals "staging"

Scenario: Test Java app with staging release stage
    When I run "ReleaseStageScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the event "app.releaseStage" equals "staging"

Scenario: Test Spring Boot app with staging release stage
    When I run spring boot "ReleaseStageScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the event "app.releaseStage" equals "staging"

Scenario: Test Spring app with staging release stage
    When I run plain Spring "ReleaseStageScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the event "app.releaseStage" equals "staging"
