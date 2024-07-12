Feature: Metadata is redacted

Scenario: Using the default metadata redactedKeys
    When I run "AutoRedactedKeysScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the exception "message" equals "AutoRedactedKeysScenario"
    And the event "metaData.custom.foo" equals "hunter2"
    And the event "metaData.custom.password" equals "[REDACTED]"
    And the event "metaData.user.password" equals "[REDACTED]"

Scenario: Adding a custom metadata redactedKeys
    When I run "ManualRedactedKeysScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the exception "message" equals "ManualRedactedKeysScenario"
    And the event "metaData.custom.foo" equals "[REDACTED]"
    And the event "metaData.user.abcd" equals "[REDACTED]"
    And the event "metaData.custom.bar" equals "hunter2"

Scenario: Adding a thread metadata redactedKeys using logback
    When I run "LogbackThreadMetaDataScenario" with logback config "meta_data_redactedKeys_config.xml"
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the exception "message" equals "LogbackThreadMetaDataScenario"
    And the event "metaData.thread.foo" equals "[REDACTED]"
    And the event "metaData.thread.bar" equals "threadvalue2"

Scenario: Adding a custom metadata redactedKeys using logback
    When I run "LogbackMetaDataScenario" with logback config "meta_data_redactedKeys_config.xml"
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the exception "message" equals "LogbackMetaDataScenario"
    And the event "metaData.custom.foo" equals "[REDACTED]"
    And the event "metaData.user.foo" equals "[REDACTED]"
    And the event "metaData.custom.bar" equals "hunter2"

Scenario: Using the default metadata redactedKeys in Spring Boot app
    When I run spring boot "AutoRedactedKeysScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the exception "message" equals "AutoRedactedKeysScenario"
    And the event "metaData.custom.foo" equals "hunter2"
    And the event "metaData.custom.password" equals "[REDACTED]"
    And the event "metaData.user.password" equals "[REDACTED]"

Scenario: Using the default metadata redactedKeys in Spring app
    When I run plain Spring "AutoRedactedKeysScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the exception "message" equals "AutoRedactedKeysScenario"
    And the event "metaData.custom.foo" equals "hunter2"
    And the event "metaData.custom.password" equals "[REDACTED]"
    And the event "metaData.user.password" equals "[REDACTED]"
