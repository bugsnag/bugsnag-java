Feature: Session Tracking

Scenario: Test unhandled Exception with Session information in plain Java app
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

Scenario: Test unhandled Exception with Session information in Spring Boot app
    When I run spring boot "UnhandledSessionScenario" with the defaults
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

Scenario: Test unhandled Exception with Session information in plain Spring app
    When I run plain Spring "UnhandledSessionScenario" with the defaults
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

Scenario: Test unhandled exception with no session information in plain Java app
    When I run "CrashHandlerScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the event "session" is null

Scenario: Test unhandled exception with no session information in Spring Boot app
    When I run spring boot "CrashHandlerScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the event "session" is null

Scenario: Test unhandled exception with no session information in plain Spring app
    When I run plain Spring "CrashHandlerScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the event "session" is null
