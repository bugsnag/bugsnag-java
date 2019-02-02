Feature: Session Tracking

Scenario: Manual Session sends in plain Java app
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

Scenario: Manual Session sends in spring boot app
    When I run spring boot "ManualSessionScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the session tracking API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "sessionCounts" is an array with 1 element
    And the payload field "sessionCounts.0.startedAt" is not null
    And the payload field "sessionCounts.0.sessionsStarted" equals 1
    And the payload field "app" is not null
    And the payload field "app.version" equals "1.2.3"
    And the payload field "device" is not null

Scenario: Manual Session sends in plain Spring app
    When I run plain Spring "ManualSessionScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the session tracking API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "sessionCounts" is an array with 1 element
    And the payload field "sessionCounts.0.startedAt" is not null
    And the payload field "sessionCounts.0.sessionsStarted" equals 1
    And the payload field "app" is not null
    And the payload field "app.version" equals "1.2.3"
    And the payload field "device" is not null

Scenario: Test handled Exception with Session information in plain Java app
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

Scenario: Test handled Exception with Session information in spring boot app
    When I run spring boot "HandledSessionScenario" with the defaults
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

Scenario: Test handled Exception with Session information in plain Spring app
    When I run plain Spring "HandledSessionScenario" with the defaults
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

Scenario: Test handled Exception with no session information in plain Java app
    When I run "HandledExceptionScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the event "session" is null

Scenario: Test handled Exception with no session information in Spring Boot app
    When I run spring boot "HandledExceptionScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the event "session" is null

Scenario: Test handled Exception with no session information in plain Spring app
    When I run plain Spring "HandledExceptionScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the event "session" is null
