-   Add your API key to `src/main/example/Example.jar`

-   Run maven

    ```shell
    mvn clean compile assembly:single
    ```

-   Run the example

    ```shell
    java -jar target/example-0.0.1-SNAPSHOT-jar-with-dependencies.jar
    ```
