Feature: Using the bugsnag-desktop-plugin

Scenario: Sends an unhandled exception which includes device id in session and the event
    When I run "DesktopPluginScenario" with the defaults
    And I wait to receive an error
    And I wait to receive a session
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    Then the session is valid for the session reporting API version "1.0" for the "Bugsnag Java" notifier
    And the event "metaData.device.id" is not null
    And the session payload field "device.id" is not null