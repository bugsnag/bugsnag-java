require 'net/http'

When("I run {string} with the defaults") do |eventType|
  steps %Q{
    And I set environment variable "MOCK_API_PATH" to "http://localhost:$MOCK_API_PORT"
    And I set environment variable "BUGSNAG_API_KEY" to "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And I set environment variable "EVENT_TYPE" to "#{eventType}"
    And I run the script "features/scripts/run-java-app.sh" synchronously
  }
end

When("I run {string} with logback config {string}") do |eventType, logback_config|
  steps %Q{
    And I set environment variable "MOCK_API_PATH" to "http://localhost:$MOCK_API_PORT"
    And I set environment variable "BUGSNAG_API_KEY" to "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And I set environment variable "EVENT_TYPE" to "#{eventType}"
    And I set environment variable "LOGBACK_FILE" to "#{logback_config}"
    And I run the script "features/scripts/run-java-app.sh" synchronously
  }
end

When("I run spring boot {string} with the defaults") do |eventType|
  steps %Q{
    And I set environment variable "MOCK_API_PATH" to "http://localhost:$MOCK_API_PORT"
    And I set environment variable "BUGSNAG_API_KEY" to "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And I set environment variable "EVENT_TYPE" to "#{eventType}"
    And I run the script "features/scripts/run-java-spring-boot-app.sh" synchronously
  }
end

Given("I run the plain spring app") do
  steps %Q{
    And I set environment variable "MOCK_API_PATH" to "http://#{current_ip}:#{MOCK_API_PORT}"
    And I set environment variable "BUGSNAG_API_KEY" to "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And I start the service "plainspringapp"
    And I wait for the app to respond on port "1235"
  }
end

When("I wait for the app to respond on port {string}") do |port|
  max_attempts = ENV.include?('MAX_MAZE_CONNECT_ATTEMPTS')? ENV['MAX_MAZE_CONNECT_ATTEMPTS'].to_i : 10
  attempts = 0
  up = false
  until (attempts >= max_attempts) || up
    attempts += 1
    begin
      uri = URI("http://localhost:#{port}/")
      response = Net::HTTP.get_response(uri)
      up = (response.code == "200")
    rescue EOFError
    end
    sleep 1
  end
  raise "App not ready in time!" unless up
end

When("I navigate to the route {string} on port {string}") do |route, port|
  steps %Q{
    When I open the URL "http://localhost:#{port}#{route}"
    And I wait for 1 second
  }
end

Then("the request used the Spring notifier") do
  bugsnag_regex = /^http(s?):\/\/www.bugsnag.com/
  steps %Q{
    Then the payload field "notifier.name" equals "Bugsnag Spring"
    And the payload field "notifier.url" equals "https://github.com/bugsnag/bugsnag-java"
  }
end