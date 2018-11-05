Feature: Metadata is filtered

Scenario: Using the default metadata filter
    When I run "AutoFilterScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the exception "message" equals "AutoFilterScenario"
    And the event "metaData.custom.foo" equals "hunter2"
    And the event "metaData.custom.password" equals "[FILTERED]"
    And the event "metaData.user.password" equals "[FILTERED]"

Scenario: Adding a custom metadata filter
    When I run "ManualFilterScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the exception "message" equals "ManualFilterScenario"
    And the event "metaData.custom.foo" equals "[FILTERED]"
    And the event "metaData.user.foo" equals "[FILTERED]"
    And the event "metaData.custom.bar" equals "hunter2"

Scenario: Adding a thread metadata filter using logback
    When I run "LogbackThreadMetaDataScenario" with logback config "meta_data_filter_config.xml"
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the exception "message" equals "LogbackThreadMetaDataScenario"
    And the event "metaData.thread.foo" equals "[FILTERED]"
    And the event "metaData.thread.bar" equals "threadvalue2"

Scenario: Adding a custom metadata filter using logback
    When I run "LogbackMetaDataScenario" with logback config "meta_data_filter_config.xml"
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the "Bugsnag-API-Key" header equals "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And the exception "message" equals "LogbackMetaDataScenario"
    And the event "metaData.custom.foo" equals "[FILTERED]"
    And the event "metaData.user.foo" equals "[FILTERED]"
    And the event "metaData.custom.bar" equals "hunter2"

Scenario: Using the default metadata filter in Spring Boot app
    When I run spring boot "AutoFilterScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the exception "message" equals "AutoFilterScenario"
    And the event "metaData.custom.foo" equals "hunter2"
    And the event "metaData.custom.password" equals "[FILTERED]"
    And the event "metaData.user.password" equals "[FILTERED]"

Scenario: Using the default metadata filter in Spring app
    When I run plain Spring "AutoFilterScenario" with the defaults
    Then I should receive a request
    And the request is a valid for the error reporting API
    And the exception "message" equals "AutoFilterScenario"
    And the event "metaData.custom.foo" equals "hunter2"
    And the event "metaData.custom.password" equals "[FILTERED]"
    And the event "metaData.user.password" equals "[FILTERED]"
