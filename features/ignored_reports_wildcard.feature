Feature: Reports are ignored with wildcard patterns

Scenario: Exception classname ignored with wildcard in plain Java app
    When I run "IgnoredExceptionWildcardScenario" with the defaults
    Then I should receive no errors

Scenario: Exception classname ignored with wildcard in spring boot app
    When I run spring boot "IgnoredExceptionWildcardScenario" with the defaults
    Then I should receive no errors

Scenario: Exception classname ignored with wildcard in plain spring app
    When I run plain Spring "IgnoredExceptionWildcardScenario" with the defaults
    Then I should receive no errors

Scenario: Test logback appender with wildcard pattern for ignored error class
    When I run "LogbackScenario" with logback config "ignored_class_wildcard_config.xml"
    Then I should receive no errors
