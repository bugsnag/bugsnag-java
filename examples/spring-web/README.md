# Bugsnag Spring web Java Example

Demonstrates how to use Bugsnag in a Spring Boot web based Java application.

1. Open `Config` and alter the value of `bugsnag = new Bugsnag("YOUR-API-KEY");` to match your API key

2. Run the app

    ```shell
    ../../gradlew clean bootRun
    ```

3. Cause a crash by visiting [http://localhost:8080](http://localhost:8080)

4. View the captured errors in [your dashboard](https://app.bugsnag.com)
