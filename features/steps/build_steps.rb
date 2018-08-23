When("I run {string} with the defaults") do |eventType|
  steps %Q{
    And I set environment variable "BUGSNAG_API_KEY" to "a35a2a72bd230ac0aa0f52715bbdc6aa"
    And I set environment variable "EVENT_TYPE" to "#{eventType}"
    And I run the script "run-java-app.sh" synchronously
  }
end

