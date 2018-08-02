# Bugsnag Spring web Java Example

A Spring Boot example application to show how to use Bugsnag in a Spring web based Java application.

## Running Locally

- Start the web server

    ```shell
    ../../gradlew clean bootRun
    ```

- Cause a crash

    http://localhost:8080/

## Running in Docker

Execute the following commands in the root of the repository:

```
docker-compose build
docker-compose up spring-web
```
