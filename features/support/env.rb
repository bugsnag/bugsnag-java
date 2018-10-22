# Configure app environment

require 'os'

def current_ip
  if OS.mac?
    'host.docker.internal'
  else
    ip_addr = `ifconfig | grep -Eo 'inet (addr:)?([0-9]*\\\.){3}[0-9]*' | grep -v '127.0.0.1'`
    ip_list = /((?:[0-9]*\.){3}[0-9]*)/.match(ip_addr)
    ip_list.captures.first
  end
end

# Install latest versions of the notifiers and clean fixtures
run_required_commands([
  ["mkdir", "-p", "features/fixtures/libs"],
  ["./gradlew", "clean", "bugsnag:assemble", "-Pversion=9.9.9-test"],
  ["cp", "bugsnag/build/libs/bugsnag-9.9.9-test.jar",
   "features/fixtures/libs/bugsnag-9.9.9-test.jar"],
  ["./gradlew", "clean", "bugsnag-spring:assemble", "-Pversion=9.9.9-test"],
  ["cp", "bugsnag-spring/build/libs/bugsnag-spring-9.9.9-test.jar",
   "features/fixtures/libs/bugsnag-spring-9.9.9-test.jar"],
  ["./gradlew", "-p", "features/fixtures", "clean"],
])
