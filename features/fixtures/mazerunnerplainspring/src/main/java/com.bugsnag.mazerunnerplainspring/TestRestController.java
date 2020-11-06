package com.bugsnag.mazerunnerplainspring;

import com.bugsnag.Bugsnag;
import com.bugsnag.mazerunner.scenarios.Scenario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

@RestController
public class TestRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestRestController.class);

    @Autowired
    private Bugsnag bugsnag;

    @Autowired
    private AsyncMethodService asyncMethodService;

    @RequestMapping("/")
    public String ping() {
        return "Plain Spring Fixture app ready for connections";
    }

    @RequestMapping("/send-unhandled-exception")
    public void sendUnhandledException() {
        throw new RuntimeException("Unhandled exception from TestRestController");
    }

    @RequestMapping("/add-session")
    public void addSession() {
        // A session should be automatically recorded by Bugsnag if automatic sessions are enabled
        LOGGER.info("Starting a new session");

        // Flush sessions now, otherwise need to wait for sessions to be automatically flushed
        flushAllSessions();
    }

    @RequestMapping("/run-async-task")
    public void runAsyncTask() {
        try {
            asyncMethodService.doSomethingAsync();
        } catch (Exception ex) {
            // This should not happen
            LOGGER.info("Saw exception from async call");
        }
    }

    @RequestMapping("/run-scenario/{scenario}")
    public void runScenario(@PathVariable String scenario) {
        try {
            Class clz = Class.forName("com.bugsnag.mazerunner.scenarios." + scenario);
            Constructor constructor = clz.getConstructors()[0];
            ((Scenario) constructor.newInstance(bugsnag)).run();
        } catch (Exception ex) {
            LOGGER.error("Error getting scenario", ex);
        }
    }

    /**
     * Flushes sessions from the Bugsnag object
     */
    private void flushAllSessions() {
        try {
            Field field = bugsnag.getClass().getDeclaredField("sessionTracker");
            field.setAccessible(true);
            Object sessionTracker = field.get(bugsnag);

            field = sessionTracker.getClass().getDeclaredField("enqueuedSessionCounts");
            field.setAccessible(true);
            Collection sessionCounts = (Collection) field.get(sessionTracker);

            // Flush the sessions
            Method method = sessionTracker.getClass().getDeclaredMethod("flushSessions", Date.class);
            method.setAccessible(true);
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MINUTE, 2);
            method.invoke(sessionTracker, calendar.getTime());

            // Wait until sessions are flushed
            while (sessionCounts.size() > 0) {
                Thread.sleep(1000);
            }
        } catch (Exception ex) {
            LOGGER.error("failed to flush sessions", ex);
        }
    }
}
