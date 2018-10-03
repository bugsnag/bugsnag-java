package com.bugsnag.mazerunnerspring;

import org.apache.log4j.Logger;
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

    private static final Logger LOGGER = Logger.getLogger(TestCaseRunner.class);

    private ApplicationContext ctx;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

    @Override
    public void run(String... args) throws Exception {
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
        Scenario s = testCaseForName(System.getProperty("EVENT_TYPE"));
        if (s != null) {
            LOGGER.info("running test case");
            s.run();
        } else {
            LOGGER.error("No test case found for " + System.getProperty("EVENT_TYPE"));
        }

        // Exit the application
        LOGGER.info("Exiting spring");
        SpringApplication.exit(ctx, new ExitCodeGenerator() {
            @Override
            public int getExitCode() {
                return 0;
            }
        });
        System.exit(0);
    }

    private static Scenario testCaseForName(String eventType) {

        try {
            Class clz = Class.forName("com.bugsnag.mazerunnerspring.scenarios." + eventType);
            Constructor constructor = clz.getConstructors()[0];
            return (Scenario) constructor.newInstance();
        } catch(Exception ex) {
            LOGGER.error("Error getting scenario", ex);
            return null;
        }
    }
}
