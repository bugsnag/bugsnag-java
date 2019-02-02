Feature: Reporting metadata

Scenario: Sends a handled exception which includes custom metadata added in a notify callback
    When I run "MetaDataScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the event "metaData.Custom.foo" equals "Hello World!"

Scenario: Sends a handled exception which includes custom metadata added in a notify callback for Spring Boot app
    When I run spring boot "MetaDataScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the event "metaData.device.springVersion" is not null
    And the event "metaData.device.springBootVersion" is not null
    And the event "metaData.Custom.foo" equals "Hello World!"

Scenario: Sends a handled exception which includes custom metadata added in a notify callback for plain Spring app
    When I run plain Spring "MetaDataScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the event "metaData.Custom.foo" equals "Hello World!"
    And the event "metaData.device.springVersion" is not null
    And the event "metaData.device.springBootVersion" is null

Scenario: Test logback appender with meta data in the config file
    When I run "LogbackScenario" with logback config "meta_data_config.xml"
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.configTab.foo" equals "tabValue1"
    And the event "metaData.configTab.bar" equals "tabValue2"

Scenario: Test logback appender with thread meta data
    When I run "LogbackThreadMetaDataScenario" with logback config "basic_config.xml"
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.thread.foo" equals "threadvalue1"
    And the event "metaData.thread.bar" equals "threadvalue2"

Scenario: Test logback appender with meta data in a callback
    When I run "LogbackMetaDataScenario" with logback config "basic_config.xml"
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.user.foo" equals "hunter2"
    And the event "metaData.custom.foo" equals "hunter2"
    And the event "metaData.custom.bar" equals "hunter2"

Scenario: Test logback appender with meta data from the MDC
    When I run "LogbackMDCScenario" with logback config "basic_config.xml"
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.Context.foo" equals "hunter2"
    And the event "metaData.Context.bar" equals "hunter2"

Scenario: Test thread meta data
    When I run "ThreadMetaDataScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.Custom.test" equals "Global value"
    And the event "metaData.Custom.foo" equals "Thread value"
    And the event "metaData.Custom.bar" equals "Hello World!"
    And the event "metaData.Custom.something" is null

Scenario: Test thread meta data for Spring Boot app
    When I run spring boot "ThreadMetaDataScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.Custom.test" equals "Global value"
    And the event "metaData.Custom.foo" equals "Thread value"
    And the event "metaData.Custom.bar" equals "Hello World!"
    And the event "metaData.Custom.something" is null

Scenario: Test thread meta data for plain Spring app
    When I run plain Spring "ThreadMetaDataScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.Custom.test" equals "Global value"
    And the event "metaData.Custom.foo" equals "Thread value"
    And the event "metaData.Custom.bar" equals "Hello World!"
    And the event "metaData.Custom.something" is null

Scenario: Test unhandled thread meta data
    When I run "UnhandledThreadMetaDataScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.Custom.test" equals "Global value"
    And the event "metaData.Custom.foo" equals "Thread value 1"
    And the event "metaData.Custom.bar" equals "Thread value 2"
    And the event "metaData.Custom.something" is null

Scenario: Test unhandled thread meta data for Spring Boot app
    When I run spring boot "UnhandledThreadMetaDataScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.Custom.test" equals "Global value"
    And the event "metaData.Custom.foo" equals "Thread value 1"
    And the event "metaData.Custom.bar" equals "Thread value 2"
    And the event "metaData.Custom.something" is null

Scenario: Test unhandled thread meta data for plain Spring app
    When I run plain Spring "UnhandledThreadMetaDataScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.Custom.test" equals "Global value"
    And the event "metaData.Custom.foo" equals "Thread value 1"
    And the event "metaData.Custom.bar" equals "Thread value 2"
    And the event "metaData.Custom.something" is null

Scenario: Test logback appender with thread meta data
    When I run "LogbackThreadMetaDataScenario" with logback config "basic_config.xml"
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.thread.foo" equals "threadvalue1"
    And the event "metaData.thread.bar" equals "threadvalue2"
    And the event "metaData.Custom.something" is null

Scenario: Test thread meta data in plain spring async method
    Given I run the plain spring app
    When I navigate to the route "/mazerunnerplainspring/run-async-task" on port "1235"
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the event "metaData.thread.key1" is null
    And the event "metaData.thread.key2" equals "should be included in meta data"

Scenario: Test thread meta data in spring boot async method
    When I run spring boot "AsyncMethodScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the event "metaData.thread.key1" is null
    And the event "metaData.thread.key2" equals "should be included in meta data"

Scenario: Test thread meta data in plain spring scheduled task
    Given I set environment variable "RUN_SCHEDULED_TASK" to "true"
    And I run the plain spring app
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the event "metaData.thread.key1" is null
    And the event "metaData.thread.key2" equals "should be included in meta data"

Scenario: Test thread meta data in spring boot scheduled task
    When I run spring boot "ScheduledTaskScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the event "metaData.thread.key1" is null
    And the event "metaData.thread.key2" equals "should be included in meta data"


