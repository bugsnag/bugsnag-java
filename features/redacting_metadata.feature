Feature: Metadata is redacted

Scenario: Using the default metadata redaction
    When I run "AutoRedactScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the exception "message" equals "AutoRedactScenario"
    And the event "metaData.custom.foo" equals "hunter2"
    And the event "metaData.custom.password" equals "[REDACTED]"
    And the event "metaData.user.password" equals "[REDACTED]"

Scenario: Adding a custom metadata redaction
    When I run "ManualRedactScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the exception "message" equals "ManualRedactScenario"
    And the event "metaData.custom.foo" equals "[REDACTED]"
    And the event "metaData.user.foo" equals "[REDACTED]"
    And the event "metaData.custom.bar" equals "hunter2"

Scenario: Adding a thread metadata redaction using logback
    When I run "LogbackThreadMetaDataScenario" with logback config "meta_data_redact_config.xml"
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the exception "message" equals "LogbackThreadMetaDataScenario"
    And the event "metaData.thread.foo" equals "[REDACTED]"
    And the event "metaData.thread.bar" equals "threadvalue2"

Scenario: Adding a custom metadata redaction using logback
    When I run "LogbackMetaDataScenario" with logback config "meta_data_redact_config.xml"
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the exception "message" equals "LogbackMetaDataScenario"
    And the event "metaData.custom.foo" equals "[REDACTED]"
    And the event "metaData.user.foo" equals "[REDACTED]"
    And the event "metaData.custom.bar" equals "hunter2"

Scenario: Using the default metadata redaction in Spring Boot app
    When I run spring boot "AutoRedactScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the exception "message" equals "AutoRedactScenario"
    And the event "metaData.custom.foo" equals "hunter2"
    And the event "metaData.custom.password" equals "[REDACTED]"
    And the event "metaData.user.password" equals "[REDACTED]"

Scenario: Using the default metadata redaction in Spring app
    When I run plain Spring "AutoRedactScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the exception "message" equals "AutoRedactScenario"
    And the event "metaData.custom.foo" equals "hunter2"
    And the event "metaData.custom.password" equals "[REDACTED]"
    And the event "metaData.user.password" equals "[REDACTED]"

