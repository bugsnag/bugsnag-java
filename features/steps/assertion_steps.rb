Then("the request used the Spring notifier") do
  steps %Q{
    Then the payload field "notifier.name" equals "Bugsnag Spring"
    And the payload field "notifier.url" equals "https://github.com/bugsnag/bugsnag-java"
  }
end

