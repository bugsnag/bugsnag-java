Feature: Runtime versions are included in all requests

### Errors

Scenario: Runtime versions included in Plain Java error
    When I run "HandledExceptionScenario" with the defaults
    Then I should receive a request
    And the request is valid for the error reporting API
    And the payload field "events.0.device.runtimeVersions.javaType" ends with "Runtime Environment"
    And the payload field "events.0.device.runtimeVersions.javaVersion" starts with "1."

Scenario: Runtime versions included in Spring Framework error
    When I run plain Spring "HandledExceptionScenario" with the defaults
    Then I should receive a request
    And the request is valid for the error reporting API
    And the payload field "events.0.device.runtimeVersions.javaType" ends with "Runtime Environment"
    And the payload field "events.0.device.runtimeVersions.javaVersion" starts with "1."
    And the payload field "events.0.device.runtimeVersions.springFramework" matches the regex "(\d.)+"

Scenario: Runtime versions included in Spring Boot error
    When I run spring boot "HandledExceptionScenario" with the defaults
    Then I should receive a request
    And the request is valid for the error reporting API
    And the payload field "events.0.device.runtimeVersions.javaType" ends with "Runtime Environment"
    And the payload field "events.0.device.runtimeVersions.javaVersion" starts with "1."
    And the payload field "events.0.device.runtimeVersions.springFramework" matches the regex "(\d.)+"
    And the payload field "events.0.device.runtimeVersions.springBoot" matches the regex "(\d.)+"

### Sessions

Scenario: Runtime versions included in Plain Java session
    When I run "ManualSessionScenario" with the defaults
    Then I should receive a request
    And the request is valid for the session tracking API
    And the payload field "device.runtimeVersions.javaType" ends with "Runtime Environment"
    And the payload field "device.runtimeVersions.javaVersion" starts with "1."

Scenario: Runtime versions included in Spring Framework session
    When I run plain Spring "ManualSessionScenario" with the defaults
    Then I should receive a request
    And the request is valid for the session tracking API
    And the payload field "device.runtimeVersions.javaType" ends with "Runtime Environment"
    And the payload field "device.runtimeVersions.javaVersion" starts with "1."
    And the payload field "device.runtimeVersions.springFramework" matches the regex "(\d.)+"

Scenario: Runtime versions included in Spring Boot session
    When I run spring boot "ManualSessionScenario" with the defaults
    Then I should receive a request
    And the request is valid for the session tracking API
    And the payload field "device.runtimeVersions.javaType" ends with "Runtime Environment"
    And the payload field "device.runtimeVersions.javaVersion" starts with "1."
    And the payload field "device.runtimeVersions.springFramework" matches the regex "(\d.)+"
    And the payload field "device.runtimeVersions.springBoot" matches the regex "(\d.)+"
