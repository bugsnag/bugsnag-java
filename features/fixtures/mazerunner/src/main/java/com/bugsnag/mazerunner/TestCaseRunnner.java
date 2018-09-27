package com.bugsnag.mazerunner;

import com.bugsnag.Bugsnag;
import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

@Component
public class TestCaseRunnner implements CommandLineRunner, ApplicationContextAware {

    private static final Logger LOGGER = Logger.getLogger(TestCaseRunnner.class);

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
            LOGGER.info("Setting property " + argParts[0] + "=" + argParts[1]);
            System.setProperty(argParts[0], argParts[1]);
        }

        // Create and run the test case
        LOGGER.info("Creating test case");
        Scenario s = testCaseForName(System.getProperty("EVENT_TYPE"));
        if (s != null) {
            LOGGER.info("running test case");
            s.run();
        } else {
            LOGGER.info("No test case found!");
            throw new IllegalStateException("No test case found");
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
            Class clz = Class.forName("com.bugsnag.mazerunner.scenarios." + eventType);
            Constructor constructor = clz.getConstructors()[0];
            return (Scenario) constructor.newInstance();
        } catch(Exception ex) {
            return null;
        }
    }
}
