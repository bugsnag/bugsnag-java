package com.bugsnag.mazerunner;

import com.bugsnag.Bugsnag;
import com.bugsnag.delivery.Delivery;
import com.bugsnag.serialization.Serializer;
import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.Map;


public abstract class Scenario {

    private static final Logger LOGGER = Logger.getLogger(Scenario.class);

    protected Bugsnag bugsnag;

    public Scenario() {

        String apiKey = "YOUR-API-KEY";
        if (!StringUtils.isEmpty(System.getProperty("BUGSNAG_API_KEY"))) {
            apiKey = System.getProperty("BUGSNAG_API_KEY");
        }

        String path = "http://localhost:9339";
        if (!StringUtils.isEmpty(System.getProperty("MOCK_API_PATH"))) {
            path = System.getProperty("MOCK_API_PATH");
        }

        LOGGER.info("using " + path + " to send Bugsnags");

        bugsnag = new Bugsnag(apiKey);
        bugsnag.setEndpoints(path, path);
    }

    public abstract void run();

    /**
     * Returns a throwable with the message as the current classname
     */
    protected Throwable generateException(){
        return new RuntimeException(getClass().getSimpleName());
    }

    /**
     * Prevents sessions from being delivered
     */
    protected void disableSessionDelivery() {
        bugsnag.setSessionDelivery(new Delivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                // Do nothing
            }

            @Override
            public void close() {
                // Do nothing
            }
        });
    }

    /**
     * Flushes sessions from the Bugsnag object
     */
    protected void flushAllSessions() {
        try {
            Field field = bugsnag.getClass().getDeclaredField("sessionTracker");
            field.setAccessible(true);
            Object sessionTracker = field.get(bugsnag);

            Method method = sessionTracker.getClass().getDeclaredMethod("flushSessions", Date.class);
            method.setAccessible(true);
            method.invoke(sessionTracker, new Date(System.nanoTime() + 120000));

            // Wait until the sessions have been flushed
            Thread.sleep(5000);

            field = sessionTracker.getClass().getDeclaredField("enqueuedSessionCounts");
            field.setAccessible(true);
            Collection sessionCounts = (Collection) field.get(sessionTracker);
            while (sessionCounts.size() > 0) {
                Thread.sleep(1000);
            }

        } catch (java.lang.Exception ex) {
            LOGGER.error("failed to flush sessions", ex);
        }
    }
}
