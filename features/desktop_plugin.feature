Feature: Using the bugsnag-desktop-plugin

Scenario: Sends an unhandled exception which includes device id in session and the event
    When I run "DesktopPluginScenario" with the defaults
    And I wait to receive an error
    And the session is valid for a session reporting API version "1.0" for the "Bugsnag Java" notifier
    And I wait to recieve a session
    And the error is valid for the error reporting API version "4" for the "Bugsnag Java" notifier
    And the event "metaData.Device.id" is not null
    And the session payload field "deivce.id" is not null