BeforeAll do
  $api_key = "a35a2a72bd230ac0aa0f52715bbdc6aa"
  Maze.config.enforce_bugsnag_integrity = false
  Maze.config.receive_no_requests_wait = 10
  Maze.config.receive_requests_wait = 60
  Maze::Runner.run_script("features/scripts/assemble-fixtures.sh", blocking: true)
end
