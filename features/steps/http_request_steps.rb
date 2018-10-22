require 'net/http'

When("I wait for the app to respond on port {string}") do |port|
  max_attempts = 10
  attempts = 0
  up = false
  until (attempts >= max_attempts) || up
    attempts += 1
    begin
      uri = URI("http://localhost:#{port}/")
      response = Net::HTTP.get_response(uri)
      up = (response.code == "200")
    rescue EOFError, Errno::ECONNRESET
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