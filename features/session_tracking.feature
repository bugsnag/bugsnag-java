Feature: Session Tracking

Scenario: Manual Session sends
    When I run "ManualSessionScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the session tracking API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "sessionCounts" is an array with 1 element
    And the payload field "sessionCounts.0.startedAt" is not null
    And the payload field "sessionCounts.0.sessionsStarted" equals 1
    And the payload field "app" is not null
    And the payload field "app.version" equals "1.2.3"
    And the payload field "device" is not null

Scenario: Test handled Exception with Session information
    When I run "HandledSessionScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "HandledSessionScenario"
    And the payload field "events.0.session" is not null
    And the payload field "events.0.session.id" is not null
    And the payload field "events.0.session.startedAt" is not null
    And the payload field "events.0.session.events.handled" equals 1

Scenario: Test handled Exception with no session information
    When I run "HandledExceptionScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the event "session" is null

Scenario: Test unhandled Exception with Session information
    When I run "UnhandledSessionScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "UnhandledSessionScenario"
    And the payload field "events.0.session" is not null
    And the payload field "events.0.session.id" is not null
    And the payload field "events.0.session.startedAt" is not null
    And the payload field "events.0.session.events.unhandled" equals 1

Scenario: Test unhandled exception with no session information
    When I run "CrashHandlerScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the event "session" is not null