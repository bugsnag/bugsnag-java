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

Scenario: Test logback appender with meta data in a callback
    When I run "LogbackThreadMetaDataScenario" with logback config "basic_config.xml"
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.thread.foo" equals "threadvalue1"
    And the event "metaData.thread.bar" equals "threadvalue2"

Scenario: Test logback appender with thread meta data
    When I run "LogbackThreadMetaDataScenario" with logback config "basic_config.xml"
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the payload field "events" is an array with 1 element
    And the exception "errorClass" equals "java.lang.RuntimeException"
    And the event "metaData.thread.foo" equals "threadvalue1"
    And the event "metaData.thread.bar" equals "threadvalue2"