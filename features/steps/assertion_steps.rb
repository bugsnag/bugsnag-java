Then("the request used the Spring notifier") do
  steps %Q{
    Then the payload field "notifier.name" equals "Bugsnag Spring"
    And the payload field "notifier.url" equals "https://github.com/bugsnag/bugsnag-java"
  }
end

Then(/^the event "(.+)" equals "(.+)" for request (\d+)$/) do |field, string_value, request_index|
  step "the payload field \"events.0.#{field}\" equals \"#{string_value}\" for request #{request_index}"
end

Then(/^the event "(.+)" is null for request (\d+)$/) do |field, request_index|
  step "the payload field \"events.0.#{field}\" is null for request #{request_index}"
end

Then(/^the exception "(.+)" equals "(.+)" for request (\d+)$/) do |field, string_value, request_index|
  step "the payload field \"events.0.exceptions.0.#{field}\" equals \"#{string_value}\" for request #{request_index}"
end


