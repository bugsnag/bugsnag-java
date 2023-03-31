Feature: Session Tracking

Scenario: Manual Session sends in plain Java app
    When I run "ManualSessionScenario" with the defaults
    And I wait to receive a session
    Then the session is valid for the session reporting API version "1.0" for the "Bugsnag Java" notifier
    And the session payload field "sessionCounts" is an array with 1 elements
    And the session payload field "sessionCounts.0.startedAt" is not null
    And the session payload field "sessionCounts.0.sessionsStarted" equals 1
    And the session payload field "app" is not null
    And the session payload field "app.version" equals "1.2.3"
    And the session payload field "device" is not null

Scenario: Manual Session sends in spring boot app
    When I run spring boot "ManualSessionScenario" with the defaults
    And I wait to receive a session
    Then the session is valid for the session reporting API version "1.0" for the "Bugsnag Spring" notifier
    And the session payload field "sessionCounts" is an array with 1 elements
    And the session payload field "sessionCounts.0.startedAt" is not null
    And the session payload field "sessionCounts.0.sessionsStarted" equals 1
    And the session payload field "app" is not null
    And the session payload field "app.version" equals "1.2.3"
    And the session payload field "device" is not null

Scenario: Manual Session sends in plain Spring app
    When I run plain Spring "ManualSessionScenario" with the defaults
    And I wait to receive a session
    Then the session is valid for the session reporting API version "1.0" for the "Bugsnag Spring" notifier
    And the session payload field "sessionCounts" is an array with 1 elements
    And the session payload field "sessionCounts.0.startedAt" is not null
    And the session payload field "sessionCounts.0.sessionsStarted" equals 1
    And the session payload field "app" is not null
    And the session payload field "app.version" equals "1.2.3"
    And the session payload field "device" is not null

Scenario: Test handled Exception with Session information in plain Java app
    When I run "HandledSessionScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "HandledSessionScenario"
    And the error payload field "events.0.session" is not null
    And the error payload field "events.0.session.id" is not null
    And the error payload field "events.0.session.startedAt" is not null
    And the error payload field "events.0.session.events.handled" equals 1

Scenario: Test handled Exception with Session information in spring boot app
    When I run spring boot "HandledSessionScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "HandledSessionScenario"
    And the error payload field "events.0.session" is not null
    And the error payload field "events.0.session.id" is not null
    And the error payload field "events.0.session.startedAt" is not null
    And the error payload field "events.0.session.events.handled" equals 1

Scenario: Test handled Exception with Session information in plain Spring app
    When I run plain Spring "HandledSessionScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "HandledSessionScenario"
    And the error payload field "events.0.session" is not null
    And the error payload field "events.0.session.id" is not null
    And the error payload field "events.0.session.startedAt" is not null
    And the error payload field "events.0.session.events.handled" equals 1

Scenario: Test handled Exception with no session information in plain Java app
    When I run "HandledExceptionScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the event "session" is null

Scenario: Test handled Exception with no session information in Spring Boot app
    When I run spring boot "HandledExceptionScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the event "session" is null

Scenario: Test handled Exception with no session information in plain Spring app
    When I run plain Spring "HandledExceptionScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the event "session" is null
