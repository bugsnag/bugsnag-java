# Bugsnag Logback Java Example

Demonstrates how to use the Bugsnag Logback appender in a Spring Boot application.

1. Change the value of `<apiKey>` in `resources/logback.xml` to match your API key

2. Build the app

    ```shell
    ../../gradlew clean assemble
    ```

3. Run the app

    ```shell
    ../../gradlew run
    ```

4. View the captured errors in [your dashboard](https://app.bugsnag.com)
