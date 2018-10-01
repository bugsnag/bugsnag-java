# Configure app environment


# Install latest versions of bugsnag-java
run_required_commands([
  ["./gradlew", "clean", ":assemble", "-Pversion=9.9.9-test"],
  ["mkdir", "-p", "features/fixtures/mazerunner/libs"],
  ["cp", "build/libs/bugsnag-java-9.9.9-test.jar",
   "features/fixtures/mazerunner/libs/bugsnag-java-9.9.9-test.jar"],
])

# Build the harness app
Dir.chdir('features/fixtures/mazerunner') do
  run_required_commands([
    ["./gradlew", "clean", "build"],
  ])
end

