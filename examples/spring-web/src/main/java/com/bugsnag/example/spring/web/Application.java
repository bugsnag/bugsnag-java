package com.bugsnag.example.spring.web;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Kicks off the Spring Boot application.
 */
@SpringBootApplication
public class Application {

    private static final Logger LOGGER = Logger.getLogger(Application.class);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class, args);

        LOGGER.info("Now visit http://localhost:8080 in your web browser");
    }
}
