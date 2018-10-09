Feature: Auto Session Tracking

Scenario: Report automatic session from a spring app
    When I run spring boot "AutoSessionScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the session tracking API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "sessionCounts" is an array with 1 element
    And the payload field "sessionCounts.0.startedAt" is not null
    And the payload field "sessionCounts.0.sessionsStarted" equals 1
    And the payload field "app" is not null
    And the payload field "app.version" equals "1.0.0"