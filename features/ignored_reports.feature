Feature: Reports are ignored

Scenario: Exception classname ignored
    When I run "IgnoredExceptionScenario" with the defaults
    Then I should receive no requests

Scenario: Disabled Exception Handler
    When I run "DisableAutoNotifyScenario" with the defaults
    Then I should receive no requests

Scenario: Test logback appender with ignored error class
    When I run "LogbackScenario" with logback config "ignored_class_config.xml"
    Then I should receive no requests