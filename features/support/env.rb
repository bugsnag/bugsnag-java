# Configure app environment


# Install latest versions of bugsnag-java
run_required_commands([
  ["mkdir", "-p", "features/fixtures/mazerunner/libs"],
  ["mkdir", "-p", "features/fixtures/mazerunnerspring/libs"],
  ["./gradlew", "clean", "bugsnag:assemble", "-Pversion=9.9.9-test"],
  ["cp", "bugsnag/build/libs/bugsnag-9.9.9-test.jar",
   "features/fixtures/mazerunner/libs/bugsnag-9.9.9-test.jar"],
  ["cp", "bugsnag/build/libs/bugsnag-9.9.9-test.jar",
   "features/fixtures/mazerunnerspring/libs/bugsnag-9.9.9-test.jar"],
  ["./gradlew", "clean", "bugsnag-spring:assemble", "-Pversion=9.9.9-test"],
  ["cp", "bugsnag-spring/build/libs/bugsnag-spring-9.9.9-test.jar",
   "features/fixtures/mazerunnerspring/libs/bugsnag-spring-9.9.9-test.jar"],
])

# Build the harness app
Dir.chdir('features/fixtures/mazerunner') do
  run_required_commands([
    ["./gradlew", "clean", "build"],
  ])
end

# Build the spring harness app
Dir.chdir('features/fixtures/mazerunnerspring') do
  run_required_commands([
    ["./gradlew", "clean", "build"],
  ])
end

