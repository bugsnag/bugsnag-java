Feature: Auto Session Tracking

Scenario: Report automatic session from a spring boot app
    When I run spring boot "AutoSessionScenario" with the defaults
    And I wait to receive a session
    Then the session is valid for the session reporting API version "1.0" for the "Bugsnag Spring" notifier
    And the session payload field "sessionCounts" is an array with 1 elements
    And the session payload field "sessionCounts.0.startedAt" is not null
    And the session payload field "sessionCounts.0.sessionsStarted" equals 1
    And the session payload field "app" is not null
    And the session payload field "app.version" equals "1.0.0"
