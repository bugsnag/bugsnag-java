Feature: Reporting metadata

Scenario: Sends a handled exception which includes custom metadata added in a notify callback
    When I run "MetadataScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the event "metaData.Custom.foo" equals "Hello World!"

Scenario: Sends a handled exception which includes custom metadata added in a notify callback for Spring Boot app
    When I run spring boot "MetadataScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the event "metaData.Custom.foo" equals "Hello World!"

Scenario: Sends a handled exception which includes custom metadata added in a notify callback for plain Spring app
    When I run plain Spring "MetadataScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the event "metaData.Custom.foo" equals "Hello World!"

Scenario: Test logback appender with metadata in the config file
    When I run "LogbackScenario" with logback config "meta_data_config.xml"
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.configTab.foo" equals "tabValue1"
    And the event "metaData.configTab.bar" equals "tabValue2"

Scenario: Test logback appender with thread metadata
    When I run "LogbackThreadMetadataScenario" with logback config "basic_config.xml"
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.thread.foo" equals "threadvalue1"
    And the event "metaData.thread.bar" equals "threadvalue2"

Scenario: Test logback appender with metadata in a callback
    When I run "LogbackMetadataScenario" with logback config "basic_config.xml"
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.user.foo" equals "hunter2"
    And the event "metaData.custom.foo" equals "hunter2"
    And the event "metaData.custom.bar" equals "hunter2"

Scenario: Test logback appender with metadata from the MDC
    When I run "LogbackMDCScenario" with logback config "basic_config.xml"
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.Context.foo" equals "hunter2"
    And the event "metaData.Context.bar" equals "hunter2"

Scenario: Test thread metadata
    When I run "ThreadMetadataScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.Custom.test" equals "Global value"
    And the event "metaData.Custom.foo" equals "Thread value"
    And the event "metaData.Custom.bar" equals "Hello World!"
    And the event "metaData.Custom.something" is null

Scenario: Test thread metadata for Spring Boot app
    When I run spring boot "ThreadMetadataScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.Custom.test" equals "Global value"
    And the event "metaData.Custom.foo" equals "Thread value"
    And the event "metaData.Custom.bar" equals "Hello World!"
    And the event "metaData.Custom.something" is null

Scenario: Test thread metadata for plain Spring app
    When I run plain Spring "ThreadMetadataScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.Custom.test" equals "Global value"
    And the event "metaData.Custom.foo" equals "Thread value"
    And the event "metaData.Custom.bar" equals "Hello World!"
    And the event "metaData.Custom.something" is null

Scenario: Test unhandled thread metadata
    When I run "UnhandledThreadMetadataScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.Custom.test" equals "Global value"
    And the event "metaData.Custom.foo" equals "Thread value 1"
    And the event "metaData.Custom.bar" equals "Thread value 2"
    And the event "metaData.Custom.something" is null

Scenario: Test unhandled thread metadata for Spring Boot app
    When I run spring boot "UnhandledThreadMetadataScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.Custom.test" equals "Global value"
    And the event "metaData.Custom.foo" equals "Thread value 1"
    And the event "metaData.Custom.bar" equals "Thread value 2"
    And the event "metaData.Custom.something" is null

Scenario: Test unhandled thread metadata for plain Spring app
    When I run plain Spring "UnhandledThreadMetadataScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.Custom.test" equals "Global value"
    And the event "metaData.Custom.foo" equals "Thread value 1"
    And the event "metaData.Custom.bar" equals "Thread value 2"
    And the event "metaData.Custom.something" is null

Scenario: Test logback appender with thread metadata
    When I run "LogbackThreadMetadataScenario" with logback config "basic_config.xml"
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.thread.foo" equals "threadvalue1"
    And the event "metaData.thread.bar" equals "threadvalue2"
    And the event "metaData.Custom.something" is null

Scenario: Test thread meta data in plain spring async method
    Given I run the plain spring app
    When I navigate to the route "/run-async-task" on port "8080"
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the event "metaData.thread.key1" is null
    And the event "metaData.thread.key2" equals "should be included in metadata"

Scenario: Test thread meta data in spring boot async method
    When I run spring boot "AsyncMethodScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the event "metaData.thread.key1" is null
    And the event "metaData.thread.key2" equals "should be included in metadata"

Scenario: Test thread meta data in plain spring scheduled task
    Given I set environment variable "RUN_SCHEDULED_TASK" to "true"
    And I run the plain spring app
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the event "metaData.thread.key1" is null
    And the event "metaData.thread.key2" equals "should be included in metadata"

Scenario: Test thread meta data in spring boot scheduled task
    When I run spring boot "ScheduledTaskScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the event "metaData.thread.key1" is null
    And the event "metaData.thread.key2" equals "should be included in metadata"
