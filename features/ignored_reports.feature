Feature: Reports are ignored

Scenario: Exception classname ignored in plain Java app
    When I run "IgnoredExceptionScenario" with the defaults
    Then I should receive no errors

Scenario: Exception classname ignored in spring boot app
    When I run spring boot "IgnoredExceptionScenario" with the defaults
    Then I should receive no errors

Scenario: Exception classname ignored in plain spring app
    When I run plain Spring "IgnoredExceptionScenario" with the defaults
    Then I should receive no errors

Scenario: Test logback appender with ignored error class
    When I run "LogbackScenario" with logback config "ignored_class_config.xml"
    Then I should receive no errors

Scenario: Disabled Exception Handler in plain Java app
    When I run "DisableAutoNotifyScenario" with the defaults
    Then I should receive no errors

Scenario: Disabled Exception Handler in spring boot app
    When I run spring boot "DisableAutoNotifyScenario" with the defaults
    Then I should receive no errors

Scenario: Disabled Exception Handler in plain spring app
    When I run plain Spring "DisableAutoNotifyScenario" with the defaults
    Then I should receive no errors
