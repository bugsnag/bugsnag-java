# Configure app environment

require 'os'

# Install latest versions of the notifiers and build fixtures
run_required_commands([
  ["mkdir", "-p", "features/fixtures/libs"],
  ["./gradlew", "bugsnag:assemble", "-Pversion=9.9.9-test"],
  ["cp", "bugsnag/build/libs/bugsnag-9.9.9-test.jar",
   "features/fixtures/libs/bugsnag-9.9.9-test.jar"],
  ["./gradlew", "bugsnag-spring:assemble", "-Pversion=9.9.9-test"],
  ["cp", "bugsnag-spring/build/libs/bugsnag-spring-9.9.9-test.jar",
   "features/fixtures/libs/bugsnag-spring-9.9.9-test.jar"]
])
