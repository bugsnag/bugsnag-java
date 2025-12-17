Feature: Multiple wildcard patterns for ignoring reports

Scenario: Multiple wildcard patterns in plain Java app
    When I run "MultipleWildcardPatternsScenario" with the defaults
    Then I should receive 1 error
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "Should be sent"
