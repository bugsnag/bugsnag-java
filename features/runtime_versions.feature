Feature: Runtime versions are included in all requests

### Errors

Scenario: Runtime versions included in Plain Java error
    When I run "HandledExceptionScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the error payload field "events.0.device.runtimeVersions.javaType" ends with "Runtime Environment"
    And the error payload field "events.0.device.runtimeVersions.javaVersion" matches the regex "(\d.)+"

Scenario: Runtime versions included in Spring Framework error
    When I run plain Spring "HandledExceptionScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events.0.device.runtimeVersions.javaType" ends with "Runtime Environment"
    And the error payload field "events.0.device.runtimeVersions.javaVersion" matches the regex "(\d.)+"
    And the error payload field "events.0.device.runtimeVersions.springFramework" matches the regex "(\d.)+"

Scenario: Runtime versions included in Spring Boot error
    When I run spring boot "HandledExceptionScenario" with the defaults
    And I wait to receive an error
    And the error is valid for the error reporting API version "4" for the "Bugsnag Spring" notifier
    And the error payload field "events.0.device.runtimeVersions.javaType" ends with "Runtime Environment"
    And the error payload field "events.0.device.runtimeVersions.javaVersion" matches the regex "(\d.)+"
    And the error payload field "events.0.device.runtimeVersions.springFramework" matches the regex "(\d.)+"
    And the error payload field "events.0.device.runtimeVersions.springBoot" matches the regex "(\d.)+"

### Sessions

Scenario: Runtime versions included in Plain Java session
    When I run "ManualSessionScenario" with the defaults
    And I wait to receive a session
    Then the session is valid for the session reporting API version "1.0" for the "Bugsnag Java" notifier
    And the session payload field "device.runtimeVersions.javaType" ends with "Runtime Environment"
    And the session payload field "device.runtimeVersions.javaVersion" matches the regex "(\d.)+"

Scenario: Runtime versions included in Spring Framework session
    When I run plain Spring "ManualSessionScenario" with the defaults
    And I wait to receive a session
    Then the session is valid for the session reporting API version "1.0" for the "Bugsnag Spring" notifier
    And the session payload field "device.runtimeVersions.javaType" ends with "Runtime Environment"
    And the session payload field "device.runtimeVersions.javaVersion" matches the regex "(\d.)+"
    And the session payload field "device.runtimeVersions.springFramework" matches the regex "(\d.)+"

Scenario: Runtime versions included in Spring Boot session
    When I run spring boot "ManualSessionScenario" with the defaults
    And I wait to receive a session
    Then the session is valid for the session reporting API version "1.0" for the "Bugsnag Spring" notifier
    And the session payload field "device.runtimeVersions.javaType" ends with "Runtime Environment"
    And the session payload field "device.runtimeVersions.javaVersion" matches the regex "(\d.)+"
    And the session payload field "device.runtimeVersions.springFramework" matches the regex "(\d.)+"
    And the session payload field "device.runtimeVersions.springBoot" matches the regex "(\d.)+"
