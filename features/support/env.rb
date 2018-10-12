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


# Install latest versions of bugsnag-java
run_required_commands([
  ["mkdir", "-p", "features/fixtures/mazerunner/libs"],
  ["mkdir", "-p", "features/fixtures/mazerunnerspringboot/libs"],
  ["mkdir", "-p", "features/fixtures/mazerunnerplainspring/libs"],
  ["./gradlew", "clean", "bugsnag:assemble", "-Pversion=9.9.9-test"],
  ["cp", "bugsnag/build/libs/bugsnag-9.9.9-test.jar",
   "features/fixtures/mazerunner/libs/bugsnag-9.9.9-test.jar"],
  ["cp", "bugsnag/build/libs/bugsnag-9.9.9-test.jar",
   "features/fixtures/mazerunnerspringboot/libs/bugsnag-9.9.9-test.jar"],
  ["cp", "bugsnag/build/libs/bugsnag-9.9.9-test.jar",
   "features/fixtures/mazerunnerplainspring/libs/bugsnag-9.9.9-test.jar"],
  ["./gradlew", "clean", "bugsnag-spring:assemble", "-Pversion=9.9.9-test"],
  ["cp", "bugsnag-spring/build/libs/bugsnag-spring-9.9.9-test.jar",
   "features/fixtures/mazerunnerspringboot/libs/bugsnag-spring-9.9.9-test.jar"],
  ["cp", "bugsnag-spring/build/libs/bugsnag-spring-9.9.9-test.jar",
     "features/fixtures/mazerunnerplainspring/libs/bugsnag-spring-9.9.9-test.jar"],
])

# Build the harness app
Dir.chdir('features/fixtures/mazerunner') do
  run_required_commands([
    ["./gradlew", "clean", "build"],
  ])
end

# Build the spring boot harness app
Dir.chdir('features/fixtures/mazerunnerspringboot') do
  run_required_commands([
    ["./gradlew", "clean", "build"],
  ])
end

# Build the plain spring harness app
Dir.chdir('features/fixtures/mazerunnerplainspring') do
  run_required_commands([
    ["./gradlew", "clean", "build"],
  ])
end

