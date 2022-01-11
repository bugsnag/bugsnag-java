Feature: Session Tracking

Scenario: Test unhandled Exception with Session information in plain Java app
    When I run "UnhandledSessionScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "UnhandledSessionScenario"
    And the error payload field "events.0.session" is not null
    And the error payload field "events.0.session.id" is not null
    And the error payload field "events.0.session.startedAt" is not null
    And the error payload field "events.0.session.events.unhandled" equals 1

Scenario: Test unhandled Exception with Session information in Spring Boot app
    When I run spring boot "UnhandledSessionScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "UnhandledSessionScenario"
    And the error payload field "events.0.session" is not null
    And the error payload field "events.0.session.id" is not null
    And the error payload field "events.0.session.startedAt" is not null
    And the error payload field "events.0.session.events.unhandled" equals 1

Scenario: Test unhandled Exception with Session information in plain Spring app
    When I run plain Spring "UnhandledSessionScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "UnhandledSessionScenario"
    And the error payload field "events.0.session" is not null
    And the error payload field "events.0.session.id" is not null
    And the error payload field "events.0.session.startedAt" is not null
    And the error payload field "events.0.session.events.unhandled" equals 1

Scenario: Test unhandled exception with no session information in plain Java app
    When I run "CrashHandlerScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the event "session" is null

Scenario: Test unhandled exception with no session information in Spring Boot app
    When I run spring boot "CrashHandlerScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the event "session" is null

Scenario: Test unhandled exception with no session information in plain Spring app
    When I run plain Spring "CrashHandlerScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the event "session" is null
