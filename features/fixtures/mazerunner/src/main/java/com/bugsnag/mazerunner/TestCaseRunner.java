package com.bugsnag.mazerunner;

import com.bugsnag.Bugsnag;
import com.bugsnag.mazerunner.scenarios.Scenario;
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

    private Bugsnag bugsnag;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ctx = applicationContext;
    }

    @Override
    public void run(String... args) {
        setupBugsnag();

        // Create and run the test case
        LOGGER.info("Creating test case");
        Scenario scenario = testCaseForName(System.getenv("EVENT_TYPE"));
        if (scenario != null) {
            LOGGER.info("running test case");
            scenario.run();
        } else {
            LOGGER.error("No test case found for " + System.getenv("EVENT_TYPE"));
        }

        // Exit the application
        LOGGER.info("Exiting spring");
        System.exit(SpringApplication.exit(ctx, (ExitCodeGenerator) new ExitCodeGenerator() {
            @Override
            public int getExitCode() {
                return 0;
            }
        }));
    }

    private void setupBugsnag() {
        String apiKey = "YOUR-API-KEY";
        if (System.getenv("BUGSNAG_API_KEY") != null) {
            apiKey = System.getenv("BUGSNAG_API_KEY");
            LOGGER.info("got " + apiKey + " from env vars");
        }

        String path = "http://localhost:9339";
        if (System.getenv("MOCK_API_PATH") != null) {
            path = System.getenv("MOCK_API_PATH");
            LOGGER.info("got " + path + " from env vars");
        }

        LOGGER.info("using " + path + " to send Bugsnags");

        bugsnag = new Bugsnag(apiKey, true);
        bugsnag.setEndpoints(path, path);
    }

    private Scenario testCaseForName(String eventType) {

        try {
            Class clz = Class.forName("com.bugsnag.mazerunner.scenarios." + eventType);
            Constructor constructor = clz.getConstructors()[0];
            return (Scenario) constructor.newInstance(bugsnag);
        } catch (Exception ex) {
            LOGGER.error("Error getting scenario", ex);
            return null;
        }
    }
}
