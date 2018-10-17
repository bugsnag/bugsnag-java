package com.bugsnag.mazerunnerspringboot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;

@Component
public class TestCaseRunner implements CommandLineRunner, ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestCaseRunner.class);

    private ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

    @Override
    public void run(String... args) {
        // Put args into the system property so that they can be used later
        for (String arg : args) {
            String[] argParts = arg.split("=");
            if (argParts.length == 2) {
                LOGGER.info("Setting property " + argParts[0] + "=" + argParts[1]);
                System.setProperty(argParts[0], argParts[1]);
            } else {
                LOGGER.error("Invalid argument " + arg);
            }
        }

        // Create and run the test case
        LOGGER.info("Creating test case");
        Scenario scenario = testCaseForName(System.getProperty("EVENT_TYPE"));
        if (scenario != null) {
            LOGGER.info("running test case");
            scenario.run();
        } else {
            LOGGER.error("No test case found for " + System.getProperty("EVENT_TYPE"));
        }

        // Exit the application
        LOGGER.info("Exiting spring");
        System.exit(SpringApplication.exit(ctx, (ExitCodeGenerator) () -> 0));
    }

    private static Scenario testCaseForName(String eventType) {

        try {
            Class clz = Class.forName("com.bugsnag.mazerunnerspringboot.scenarios." + eventType);
            Constructor constructor = clz.getConstructors()[0];
            return (Scenario) constructor.newInstance();
        } catch (Exception ex) {
            LOGGER.error("Error getting scenario", ex);
            return null;
        }
    }
}
