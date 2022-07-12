Feature: Specifying the project packages

Scenario: Test logback appender with no project packages
    When I run "LogbackScenario" with logback config "basic_config.xml"
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
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
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
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
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
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
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "message" equals "ProjectPackageScenario"
    And the event "exceptions.0.stacktrace.0.method" equals "com.bugsnag.mazerunner.scenarios.Scenario.generateException"
    And the event "exceptions.0.stacktrace.0.inProject" is true
    And the event "exceptions.0.stacktrace.1.method" equals "com.bugsnag.mazerunner.scenarios.ProjectPackageScenario.run"
    And the event "exceptions.0.stacktrace.1.inProject" is true
    And the event "exceptions.0.stacktrace.2.method" equals "com.bugsnag.mazerunner.TestCaseRunner.run"
    And the event "exceptions.0.stacktrace.2.inProject" is true
    And the event "exceptions.0.stacktrace.3.method" equals "org.springframework.boot.SpringApplication.callRunner"
    And the event "exceptions.0.stacktrace.3.inProject" is false
