When("I run {string} with the defaults") do |eventType|
  steps %Q{
    And I set environment variable "MOCK_API_PATH" to "http://localhost:#{MOCK_API_PORT}"
    And I set environment variable "BUGSNAG_API_KEY" to "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And I set environment variable "EVENT_TYPE" to "#{eventType}"
    And I run the script "features/scripts/run-java-app.sh" synchronously
  }
end

When("I run {string} with logback config {string}") do |eventType, logback_config|
  steps %Q{
    And I set environment variable "MOCK_API_PATH" to "http://localhost:#{MOCK_API_PORT}"
    And I set environment variable "BUGSNAG_API_KEY" to "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And I set environment variable "EVENT_TYPE" to "#{eventType}"
    And I set environment variable "LOGBACK_FILE" to "#{logback_config}"
    And I run the script "features/scripts/run-java-app.sh" synchronously
  }
end

When("I run spring boot {string} with the defaults") do |eventType|
  steps %Q{
    And I set environment variable "MOCK_API_PATH" to "http://localhost:#{MOCK_API_PORT}"
    And I set environment variable "BUGSNAG_API_KEY" to "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And I set environment variable "EVENT_TYPE" to "#{eventType}"
    And I run the script "features/scripts/run-java-spring-boot-app.sh" synchronously
  }
end

Given("I run the plain spring app") do
  steps %Q{
    And I set environment variable "MOCK_API_PATH" to "http://localhost:#{MOCK_API_PORT}"
    And I set environment variable "BUGSNAG_API_KEY" to "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And I run the script "features/scripts/build-plain-spring-app.sh" synchronously
    And I wait for the app to respond on port "8080"
  }
end

When("I run plain Spring {string} with the defaults") do |eventType|
  steps %Q{
    And I run the plain spring app
    And I navigate to the route "/run-scenario/#{eventType}" on port "8080"
  }
end

