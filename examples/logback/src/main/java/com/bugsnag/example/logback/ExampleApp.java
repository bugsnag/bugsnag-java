package com.bugsnag.example.logback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExampleApp {
    /**
     * Example showing logs with exceptions which will get reported to Bugsnag.
     */
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(ExampleApp.class);

        // Logs with Throwable will be send to Bugsnag
        logger.warn("Warning will be reported to Bugsnag", new Throwable("Test warn"));
        logger.error("Error will be reported to Bugsnag", new Throwable("Test error"));

        // Logs without will not
        logger.info("Not reported to Bugsnag");
        logger.error("Also not reported to Bugsnag");
    }
}
