# Bugsnag Servlet Example

A simple example application to show how Bugsnag works in a Servlet-based Java application.

- Build the app

    ```shell
    gradle clean assemble
    ```

- Start the web server

    ```shell
    gradle appRun
    ```
- Cause a crash

    http://localhost:8080/servlet

## Running in Docker

Execute the following commands in the root of the repository:

```
docker-compose build
docker-compose up servlet
```
