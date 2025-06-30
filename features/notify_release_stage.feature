Feature: Reporting exceptions with release stages

Scenario: Exception not reported when outside release stage
    When I run "OutsideReleaseStageScenario" with the defaults
    Then I should receive no errors

Scenario: Exception not reported when outside release stage in Spring Boot app
    When I run spring boot "OutsideReleaseStageScenario" with the defaults
    Then I should receive no errors

Scenario: Exception not reported when outside release stage in plain Spring app
    When I run plain Spring "OutsideReleaseStageScenario" with the defaults
    Then I should receive no errors

Scenario: Exception reported when inside release stage
    When I run "InsideReleaseStageScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the exception "message" equals "InsideReleaseStageScenario"

Scenario: Exception reported when inside release stage in Spring Boot app
    When I run spring boot "InsideReleaseStageScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the exception "message" equals "InsideReleaseStageScenario"

Scenario: Exception reported when inside release stage in plain Spring app
    When I run plain Spring "InsideReleaseStageScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the exception "message" equals "InsideReleaseStageScenario"

Scenario: Exception not reported when release stage null in plain Java app
    When I run "NullReleaseStageScenario" with the defaults
    Then I should receive no errors

Scenario: Exception reported when release stages null in plain Java app
    When I run "NullNotifyReleaseStageScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "NullNotifyReleaseStageScenario"

Scenario: Exception reported when inside Notify release stage array in plain Java app
    When I run "ArrayNotifyReleaseStageScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "ArrayNotifyReleaseStageScenario"

Scenario: Test logback appender with ignored release stage
    When I run "LogbackScenario" with logback config "ignored_release_stage_config.xml"
    Then I should receive no errors
