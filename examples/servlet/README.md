# Bugsnag Servlet Example

Demonstrates how to use Bugsnag in a Servlet-based Java application.

1. Open `ExampleServlet` and alter the value of `bugsnag = new Bugsnag("YOUR-API-KEY");` to match your API key

2. Build the app

    ```shell
    gradle clean assemble
    ```

3. Start the web server

    ```shell
    gradle appRun
    ```

4. Cause a crash by visiting [http://localhost:8080/servlet](http://localhost:8080/servlet)

5. View the captured errors in [your dashboard](https://app.bugsnag.com)
