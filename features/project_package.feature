Feature: Specifying the project packages

Scenario: Test logback appender with no project packages
    When I run "LogbackScenario" with logback config "basic_config.xml"
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "LogbackScenario"
    And the event "exceptions.0.stacktrace.0.method" equals "com.bugsnag.mazerunner.scenarios.Scenario.generateException"
    And the event "exceptions.0.stacktrace.0.inProject" is false
    And the event "exceptions.0.stacktrace.1.method" equals "com.bugsnag.mazerunner.scenarios.LogbackScenario.run"
    And the event "exceptions.0.stacktrace.1.inProject" is false
    And the event "exceptions.0.stacktrace.2.method" equals "com.bugsnag.mazerunner.TestCaseRunner.run"
    And the event "exceptions.0.stacktrace.2.inProject" is false
    And the event "exceptions.0.stacktrace.3.method" equals "org.springframework.boot.SpringApplication.callRunner"
    And the event "exceptions.0.stacktrace.3.inProject" is false

Scenario: Test logback appender with a project package "com.bugsnag.mazerunner" defined
    When I run "LogbackScenario" with logback config "project_package_config.xml"
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the exception "message" equals "LogbackScenario"
    And the event "exceptions.0.stacktrace.0.method" equals "com.bugsnag.mazerunner.scenarios.Scenario.generateException"
    And the event "exceptions.0.stacktrace.0.inProject" is true
    And the event "exceptions.0.stacktrace.1.method" equals "com.bugsnag.mazerunner.scenarios.LogbackScenario.run"
    And the event "exceptions.0.stacktrace.1.inProject" is true
    And the event "exceptions.0.stacktrace.2.method" equals "com.bugsnag.mazerunner.TestCaseRunner.run"
    And the event "exceptions.0.stacktrace.2.inProject" is true
    And the event "exceptions.0.stacktrace.3.method" equals "org.springframework.boot.SpringApplication.callRunner"
    And the event "exceptions.0.stacktrace.3.inProject" is false

Scenario: Test plain Java app with no project packages
    When I run "HandledExceptionScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the payload field "events" is an array with 1 element
    And the exception "message" equals "HandledExceptionScenario"
    And the event "exceptions.0.stacktrace.0.method" equals "com.bugsnag.mazerunner.scenarios.Scenario.generateException"
    And the event "exceptions.0.stacktrace.0.inProject" is false
    And the event "exceptions.0.stacktrace.1.method" equals "com.bugsnag.mazerunner.scenarios.HandledExceptionScenario.run"
    And the event "exceptions.0.stacktrace.1.inProject" is false
    And the event "exceptions.0.stacktrace.2.method" equals "com.bugsnag.mazerunner.TestCaseRunner.run"
    And the event "exceptions.0.stacktrace.2.inProject" is false
    And the event "exceptions.0.stacktrace.3.method" equals "org.springframework.boot.SpringApplication.callRunner"
    And the event "exceptions.0.stacktrace.3.inProject" is false

Scenario: Test plain Java app with a project package "com.bugsnag.mazerunner" defined
    When I run "ProjectPackageScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the payload field "events" is an array with 1 element
    And the exception "message" equals "ProjectPackageScenario"
    And the event "exceptions.0.stacktrace.0.method" equals "com.bugsnag.mazerunner.scenarios.Scenario.generateException"
    And the event "exceptions.0.stacktrace.0.inProject" is true
    And the event "exceptions.0.stacktrace.1.method" equals "com.bugsnag.mazerunner.scenarios.ProjectPackageScenario.run"
    And the event "exceptions.0.stacktrace.1.inProject" is true
    And the event "exceptions.0.stacktrace.2.method" equals "com.bugsnag.mazerunner.TestCaseRunner.run"
    And the event "exceptions.0.stacktrace.2.inProject" is true
    And the event "exceptions.0.stacktrace.3.method" equals "org.springframework.boot.SpringApplication.callRunner"
    And the event "exceptions.0.stacktrace.3.inProject" is false
