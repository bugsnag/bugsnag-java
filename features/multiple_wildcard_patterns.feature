Feature: Multiple wildcard patterns for ignoring reports

Scenario: Multiple wildcard patterns in plain Java app
    When I run "MultipleWildcardPatternsScenario" with the defaults
    And I wait to receive an error
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "Should be sent"
