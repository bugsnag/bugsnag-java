Feature: Metadata is filtered

Scenario: Using the default metadata filter
    When I run "AutoFilterScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the exception "message" equals "AutoFilterScenario"
    And the event "metaData.custom.foo" equals "hunter2"
    And the event "metaData.custom.ipAddress" equals "[FILTERED]"
    And the event "metaData.user.ipAddress" equals "[FILTERED]"

Scenario: Adding a custom metadata filter
    When I run "ManualFilterScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the exception "message" equals "ManualFilterScenario"
    And the event "metaData.custom.foo" equals "[FILTERED]"
    And the event "metaData.user.foo" equals "[FILTERED]"
    And the event "metaData.custom.bar" equals "hunter2"

Scenario: Adding a thread metadata filter using logback
    When I run "LogbackThreadMetaDataScenario" with logback config "meta_data_filter_config.xml"
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the exception "message" equals "LogbackThreadMetaDataScenario"
    And the event "metaData.thread.foo" equals "[FILTERED]"
    And the event "metaData.thread.bar" equals "threadvalue2"

Scenario: Adding a custom metadata filter using logback
    When I run "LogbackMetaDataScenario" with logback config "meta_data_filter_config.xml"
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the exception "message" equals "LogbackMetaDataScenario"
    And the event "metaData.custom.foo" equals "[FILTERED]"
    And the event "metaData.user.foo" equals "[FILTERED]"
    And the event "metaData.custom.bar" equals "hunter2"

Scenario: Using the default metadata filter in Spring Boot app
    When I run spring boot "AutoFilterScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the exception "message" equals "AutoFilterScenario"
    And the event "metaData.custom.foo" equals "hunter2"
    And the event "metaData.custom.ipAddress" equals "[FILTERED]"
    And the event "metaData.user.ipAddress" equals "[FILTERED]"

Scenario: Using the default metadata filter in Spring app
    When I run plain Spring "AutoFilterScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the exception "message" equals "AutoFilterScenario"
    And the event "metaData.custom.foo" equals "hunter2"
    And the event "metaData.custom.ipAddress" equals "[FILTERED]"
    And the event "metaData.user.ipAddress" equals "[FILTERED]"
