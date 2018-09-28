# Configure app environment


# Install latest versions of bugsnag-java
run_required_commands([
  ["./gradlew", ":assemble"],
  ["mkdir", "-p", "features/fixtures/mazerunner/libs"],
  ["cp", "build/libs/bugsnag-java-3.3.0.jar",
   "features/fixtures/mazerunner/libs/bugsnag-java-3.3.0.jar"],
])

# Build the harness app
Dir.chdir('features/fixtures/mazerunner') do
  run_required_commands([
    ["./gradlew", "build"],
  ])
end

