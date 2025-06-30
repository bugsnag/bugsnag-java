Feature: Reporting metadata

Scenario: Sends a handled exception which includes custom metadata added in a notify callback
    When I run "MetaDataScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the event "metaData.Custom.foo" equals "Hello World!"

Scenario: Sends a handled exception which includes custom metadata added in a notify callback for Spring Boot app
    When I run spring boot "MetaDataScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the event "metaData.Custom.foo" equals "Hello World!"

Scenario: Sends a handled exception which includes custom metadata added in a notify callback for plain Spring app
    When I run plain Spring "MetaDataScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the event "metaData.Custom.foo" equals "Hello World!"

Scenario: Test logback appender with meta data in the config file
    When I run "LogbackScenario" with logback config "meta_data_config.xml"
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.configTab.foo" equals "tabValue1"
    And the event "metaData.configTab.bar" equals "tabValue2"

Scenario: Test logback appender with thread meta data
    When I run "LogbackThreadMetaDataScenario" with logback config "basic_config.xml"
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.thread.foo" equals "threadvalue1"
    And the event "metaData.thread.bar" equals "threadvalue2"

Scenario: Test logback appender with meta data in a callback
    When I run "LogbackMetaDataScenario" with logback config "basic_config.xml"
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.user.foo" equals "hunter2"
    And the event "metaData.custom.foo" equals "hunter2"
    And the event "metaData.custom.bar" equals "hunter2"

Scenario: Test logback appender with meta data from the MDC
    When I run "LogbackMDCScenario" with logback config "basic_config.xml"
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.Context.foo" equals "hunter2"
    And the event "metaData.Context.bar" equals "hunter2"

Scenario: Test thread meta data
    When I run "ThreadMetaDataScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.Custom.test" equals "Global value"
    And the event "metaData.Custom.foo" equals "Thread value"
    And the event "metaData.Custom.bar" equals "Hello World!"
    And the event "metaData.Custom.something" is null

Scenario: Test thread meta data for Spring Boot app
    When I run spring boot "ThreadMetaDataScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.Custom.test" equals "Global value"
    And the event "metaData.Custom.foo" equals "Thread value"
    And the event "metaData.Custom.bar" equals "Hello World!"
    And the event "metaData.Custom.something" is null

Scenario: Test thread meta data for plain Spring app
    When I run plain Spring "ThreadMetaDataScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.Custom.test" equals "Global value"
    And the event "metaData.Custom.foo" equals "Thread value"
    And the event "metaData.Custom.bar" equals "Hello World!"
    And the event "metaData.Custom.something" is null

Scenario: Test unhandled thread meta data
    When I run "UnhandledThreadMetaDataScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.Custom.test" equals "Global value"
    And the event "metaData.Custom.foo" equals "Thread value 1"
    And the event "metaData.Custom.bar" equals "Thread value 2"
    And the event "metaData.Custom.something" is null

Scenario: Test unhandled thread meta data for Spring Boot app
    When I run spring boot "UnhandledThreadMetaDataScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.Custom.test" equals "Global value"
    And the event "metaData.Custom.foo" equals "Thread value 1"
    And the event "metaData.Custom.bar" equals "Thread value 2"
    And the event "metaData.Custom.something" is null

Scenario: Test unhandled thread meta data for plain Spring app
    When I run plain Spring "UnhandledThreadMetaDataScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events" is an array with 1 elements
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.Custom.test" equals "Global value"
    And the event "metaData.Custom.foo" equals "Thread value 1"
    And the event "metaData.Custom.bar" equals "Thread value 2"
    And the event "metaData.Custom.something" is null

Scenario: Test logback appender with thread meta data
    When I run "LogbackThreadMetaDataScenario" with logback config "basic_config.xml"
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
    And the event "metaData.thread.key2" equals "should be included in meta data"

Scenario: Test thread meta data in spring boot async method
    When I run spring boot "AsyncMethodScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the event "metaData.thread.key1" is null
    And the event "metaData.thread.key2" equals "should be included in meta data"

Scenario: Test thread meta data in plain spring scheduled task
    Given I set environment variable "RUN_SCHEDULED_TASK" to "true"
    And I run the plain spring app
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the event "metaData.thread.key1" is null
    And the event "metaData.thread.key2" equals "should be included in meta data"

Scenario: Test thread meta data in spring boot scheduled task
    When I run spring boot "ScheduledTaskScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the event "metaData.thread.key1" is null
    And the event "metaData.thread.key2" equals "should be included in meta data"
