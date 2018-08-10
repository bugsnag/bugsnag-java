package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.bugsnag.delivery.Delivery;
import com.bugsnag.delivery.HttpDelivery;
import com.bugsnag.serialization.Serializer;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Test for the Bugsnag Appender
 * NOTE: Not called BugsnagAppenderTest because that throws away errors to prevent cycles
 */
public class AppenderTest {

    private static final Logger LOGGER = Logger.getLogger(AppenderTest.class.getCanonicalName());
    private static TestDelivery delivery;
    private static Delivery originalDelivery;

    /**
     * Create a new test delivery and assign it to the Bugsnag client
     */
    @Before
    public void swapDelivery() {
        Bugsnag bugsnag = Bugsnag.init("appenderApikey");
        originalDelivery = bugsnag.getDelivery();
        delivery = new TestDelivery();
        bugsnag.setDelivery(delivery);
    }

    @After
    public void revertDelivery() {
        Bugsnag bugsnag = Bugsnag.init("appenderApikey");
        bugsnag.setDelivery(originalDelivery);
    }


    @Test
    public void testSimpleException() {

        // Send a test log
        LOGGER.warn("Test exception", new RuntimeException("test"));

        // Check that one report was sent to Bugsnag
        assertEquals(1, delivery.getNotifications().size());

        // Check the correct event was created
        Notification notification = delivery.getNotifications().get(0);
        assertEquals("test", notification.getEvents().get(0).getExceptionMessage());
        assertEquals(Severity.WARNING.getValue(), notification.getEvents().get(0).getSeverity());
        assertEquals("Test exception",
                getMetaDataMap(notification,"Log event data").get("Message"));
    }

    @Test
    public void testExceptionSeverities() {

        // Send a test log
        LOGGER.info("Test exception", new RuntimeException("test"));
        LOGGER.warn("Test exception", new RuntimeException("test"));
        LOGGER.error("Test exception", new RuntimeException("test"));

        // Check that three reports were sent to Bugsnag
        assertEquals(3, delivery.getNotifications().size());

        // Check the correct event was created
        Notification notification = delivery.getNotifications().get(0);
        assertEquals(Severity.INFO.getValue(), notification.getEvents().get(0).getSeverity());

        notification = delivery.getNotifications().get(1);
        assertEquals(Severity.WARNING.getValue(), notification.getEvents().get(0).getSeverity());

        notification = delivery.getNotifications().get(2);
        assertEquals(Severity.ERROR.getValue(), notification.getEvents().get(0).getSeverity());
    }

    @Test
    public void testMetaData() {

        // Add some report and some thread meta data
        BugsnagAppender.addReportMetaData("report", "some key", "some value");
        BugsnagAppender.addThreadMetaData("thread", "some key", "some value");

        // Send three test logs
        LOGGER.warn("Test exception", new RuntimeException("test"));
        LOGGER.warn("Test exception", new RuntimeException("test"));
        BugsnagAppender.clearThreadMetaData();
        LOGGER.warn("Test exception", new RuntimeException("test"));

        // Check that three reports were sent to Bugsnag
        assertEquals(3, delivery.getNotifications().size());

        // Check the meta data is set as expected
        // Should have both report and thread meta data
        Notification notification = delivery.getNotifications().get(0);
        assertTrue(notification.getEvents().get(0).getMetaData().containsKey("report"));
        assertTrue(notification.getEvents().get(0).getMetaData().containsKey("thread"));

        // Should have just thread meta data
        notification = delivery.getNotifications().get(1);
        assertFalse(notification.getEvents().get(0).getMetaData().containsKey("report"));
        assertTrue(notification.getEvents().get(0).getMetaData().containsKey("thread"));

        // Should have neither meta data
        notification = delivery.getNotifications().get(2);
        assertFalse(notification.getEvents().get(0).getMetaData().containsKey("report"));
        assertFalse(notification.getEvents().get(0).getMetaData().containsKey("thread"));
    }

    @Test
    public void testBugsnagConfig() {

        // Get the Bugsnag instance
        Bugsnag bugsnag = Bugsnag.init("appenderApikey");

        Configuration config = getConfig(bugsnag);
        assertEquals("test", config.releaseStage);
        assertEquals("1.0.1", config.appVersion);
        assertEquals("gradleTask", config.appType);
        assertEquals(false, config.shouldAutoCaptureSessions());

        assertEquals(2, config.filters.length);
        assertEquals("password", config.filters[0]);
        assertEquals("credit_card_number", config.filters[1]);

        assertEquals(2, config.ignoreClasses.length);
        assertEquals("java.io.IOException", config.ignoreClasses[0]);
        assertEquals("com.example.Custom", config.ignoreClasses[1]);

        assertEquals(2, config.notifyReleaseStages.length);
        assertEquals("test", config.notifyReleaseStages[0]);
        assertEquals("development", config.notifyReleaseStages[1]);

        assertEquals(2, config.projectPackages.length);
        assertEquals("com.company.package1", config.projectPackages[0]);
        assertEquals("com.company.package2", config.projectPackages[1]);

        assertEquals(true, config.sendThreads);
    }



    private Configuration getConfig(Bugsnag bugsnag) {
        try {
            Field field = bugsnag.getClass().getDeclaredField("config");
            field.setAccessible(true);
            return (Configuration) field.get(bugsnag);
        } catch (java.lang.Exception ex) {
            return null;
        }
    }

    /**
     * Gets a hashmap key from the meta data in a notification
     *
     * @param notification The notification
     * @param key The key to get
     * @return The hash map
     */
    private Map<String, Object> getMetaDataMap(Notification notification, String key) {
        return ((Map<String, Object>) notification.getEvents().get(0).getMetaData().get(key));
    }

    /**
     * Class to intercept data from Bugsnag for testing
     */
    private class TestDelivery implements Delivery {

        /** The list of messages sent to Bugsnag*/
        private List<Notification> notifications = new ArrayList<Notification>();

        @Override
        public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
            notifications.add((Notification)object);
        }

        @Override
        public void close() {

        }

        public List<Notification> getNotifications() {
            return notifications;
        }
    }
}
