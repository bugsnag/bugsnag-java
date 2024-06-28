package com.bugsnag;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.bugsnag.callbacks.Callback;
import com.bugsnag.delivery.Delivery;
import com.bugsnag.logback.BugsnagMarker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;

/**
 * Test for using meta data via the Bugsnag Appender
 */
public class AppenderMetaDataTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppenderMetaDataTest.class);
    private StubNotificationDelivery delivery;
    private Delivery originalDelivery;
    private BugsnagAppender appender;

    /**
     * Create a new test delivery and assign it to the Bugsnag client
     */
    @Before
    public void swapDelivery() {
        ch.qos.logback.classic.Logger rootLogger =
                (ch.qos.logback.classic.Logger) LoggerFactory
                        .getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
        appender = (BugsnagAppender) rootLogger.getAppender("BUGSNAG");

        Bugsnag bugsnag = appender.getClient();
        originalDelivery = bugsnag.getDelivery();
        delivery = new StubNotificationDelivery();
        bugsnag.setDelivery(delivery);
    }

    /**
     * Restore the previous delivery objects after the test
     */
    @After
    public void revertDelivery() {
        Bugsnag bugsnag = appender.getClient();
        bugsnag.setDelivery(originalDelivery);
    }

    @Test
    public void testMetaDataFromLogbackFileFiltered() {

        // Send a log message
        LOGGER.warn("Test exception", new RuntimeException("test"));

        // Get the notification details
        Notification notification = delivery.getNotifications().get(0);
        assertTrue(notification.getEvents().get(0).getFilteredMetaData().containsKey("logbackTab"));
        Map<String, Object> myTab = getFilteredMetaDataMap(notification, "logbackTab");

        assertEquals("logbackValue1", myTab.get("logbackKey1"));
        assertEquals("logbackValue2", myTab.get("logbackKey2"));
    }

    @Test
    public void testMetaDataFromLogbackFileRedacted() {

        // Send a log message
        LOGGER.warn("Test exception", new RuntimeException("test"));

        // Get the notification details
        Notification notification = delivery.getNotifications().get(0);
        assertTrue(notification.getEvents().get(0).getRedactedMetaData().containsKey("logbackTab"));
        Map<String, Object> myTab = getRedactedMetaDataMap(notification, "logbackTab");

        assertEquals("logbackValue1", myTab.get("logbackKey1"));
        assertEquals("logbackValue2", myTab.get("logbackKey2"));
    }

    @Test
    @SuppressWarnings (value = "unchecked")
    public void testMetaDataTypesFiltered() {

        Bugsnag.addThreadMetaData("myTab", "string key", "string value");
        Bugsnag.addThreadMetaData("myTab", "bool key", true);
        Bugsnag.addThreadMetaData("myTab", "int key", 1);
        Bugsnag.addThreadMetaData("myTab", "float key", 1.1);

        Map<String, String> map = new HashMap<String, String>();
        map.put("key", "value");
        Bugsnag.addThreadMetaData("myTab", "object key", map);

        Integer[] array = new Integer[] {1, 2, 3, 4, 5};
        Bugsnag.addThreadMetaData("myTab", "array key", array);

        // Send a log message
        LOGGER.warn("Test exception", new RuntimeException("test"));

        // Get the notification details
        Notification notification = delivery.getNotifications().get(0);
        assertTrue(notification.getEvents().get(0).getFilteredMetaData().containsKey("myTab"));
        Map<String, Object> myTab = getFilteredMetaDataMap(notification, "myTab");

        assertEquals("string value", myTab.get("string key"));
        assertEquals(true, myTab.get("bool key"));
        assertEquals(1, myTab.get("int key"));
        assertEquals(1.1, myTab.get("float key"));
        assertEquals(map, myTab.get("object key"));
        assertThat((Integer[]) myTab.get("array key"), is(array));
    }

    @Test
    @SuppressWarnings (value = "unchecked")
    public void testMetaDataTypesRedacted() {

        Bugsnag.addThreadMetaData("myTab", "string key", "string value");
        Bugsnag.addThreadMetaData("myTab", "bool key", true);
        Bugsnag.addThreadMetaData("myTab", "int key", 1);
        Bugsnag.addThreadMetaData("myTab", "float key", 1.1);

        Map<String, String> map = new HashMap<String, String>();
        map.put("key", "value");
        Bugsnag.addThreadMetaData("myTab", "object key", map);

        Integer[] array = new Integer[] {1, 2, 3, 4, 5};
        Bugsnag.addThreadMetaData("myTab", "array key", array);

        // Send a log message
        LOGGER.warn("Test exception", new RuntimeException("test"));

        // Get the notification details
        Notification notification = delivery.getNotifications().get(0);
        assertTrue(notification.getEvents().get(0).getRedactedMetaData().containsKey("myTab"));
        Map<String, Object> myTab = getRedactedMetaDataMap(notification, "myTab");

        assertEquals("string value", myTab.get("string key"));
        assertEquals(true, myTab.get("bool key"));
        assertEquals(1, myTab.get("int key"));
        assertEquals(1.1, myTab.get("float key"));
        assertEquals(map, myTab.get("object key"));
        assertThat((Integer[]) myTab.get("array key"), is(array));
    }

    @Test
    public void testMetaDataRemoval() {

        // Add some thread meta data
        Bugsnag.addThreadMetaData("thread", "some key", "some thread value");

        // Send three test logs, the first one with report meta data added
        LOGGER.warn(new BugsnagMarker(new Callback() {
            @Override
            public void beforeNotify(Report report) {
                report.addToTab("report", "some key", "some report value");
            }
        }), "Test exception", new RuntimeException("test"));


        LOGGER.warn("Test exception", new RuntimeException("test"));
        Bugsnag.clearThreadMetaData();
        LOGGER.warn("Test exception", new RuntimeException("test"));

        // Check that three reports were sent to Bugsnag
        assertEquals(3, delivery.getNotifications().size());

        // Check the meta data is set as expected
        // Should have both report and thread meta data
        Notification notification = delivery.getNotifications().get(0);
        Report report = notification.getEvents().get(0);

        //filtered
        assertTrue(report.getFilteredMetaData().containsKey("report"));
        assertTrue(report.getFilteredMetaData().containsKey("thread"));
        assertEquals("some report value", getFilteredMetaDataMap(notification, "report").get("some key"));
        assertEquals("some thread value", getFilteredMetaDataMap(notification, "thread").get("some key"));

        //redacted
        assertTrue(report.getRedactedMetaData().containsKey("report"));
        assertTrue(report.getRedactedMetaData().containsKey("thread"));
        assertEquals("some report value", getRedactedMetaDataMap(notification, "report").get("some key"));
        assertEquals("some thread value", getRedactedMetaDataMap(notification, "thread").get("some key"));

        // Should have just thread meta data - Filtered
        notification = delivery.getNotifications().get(1);
        report = notification.getEvents().get(0);
        assertFalse(report.getFilteredMetaData().containsKey("report"));
        assertTrue(report.getFilteredMetaData().containsKey("thread"));
        assertEquals("some thread value", getFilteredMetaDataMap(notification, "thread").get("some key"));

        // Should have just thread meta data - Redacted
        notification = delivery.getNotifications().get(1);
        report = notification.getEvents().get(0);
        assertFalse(report.getRedactedMetaData().containsKey("report"));
        assertTrue(report.getRedactedMetaData().containsKey("thread"));
        assertEquals("some thread value", getRedactedMetaDataMap(notification, "thread").get("some key"));

        // Should have neither meta data - Filtered
        notification = delivery.getNotifications().get(2);
        report = notification.getEvents().get(0);
        assertFalse(report.getFilteredMetaData().containsKey("report"));
        assertFalse(report.getFilteredMetaData().containsKey("thread"));

        // Should have neither meta data - Redacted
        notification = delivery.getNotifications().get(2);
        report = notification.getEvents().get(0);
        assertFalse(report.getRedactedMetaData().containsKey("report"));
        assertFalse(report.getRedactedMetaData().containsKey("thread"));
    }


    @Test
    @SuppressWarnings (value = "unchecked")
    public void testMetaDataFromMdcFiltered() {

        MDC.put("context key1", "context value1");
        MDC.put("context key2", "context value2");

        // Send a log message
        LOGGER.warn("Test exception", new RuntimeException("test"));

        // Get the notification details
        Notification notification = delivery.getNotifications().get(0);
        assertTrue(notification.getEvents().get(0).getFilteredMetaData().containsKey("Context"));
        Map<String, Object> myTab = getFilteredMetaDataMap(notification, "Context");

        assertEquals("context value1", myTab.get("context key1"));
        assertEquals("context value2", myTab.get("context key2"));
    }

    @Test
    @SuppressWarnings (value = "unchecked")
    public void testMetaDataFromMdcRedacted() {

        MDC.put("context key1", "context value1");
        MDC.put("context key2", "context value2");

        // Send a log message
        LOGGER.warn("Test exception", new RuntimeException("test"));

        // Get the notification details
        Notification notification = delivery.getNotifications().get(0);
        assertTrue(notification.getEvents().get(0).getRedactedMetaData().containsKey("Context"));
        Map<String, Object> myTab = getRedactedMetaDataMap(notification, "Context");

        assertEquals("context value1", myTab.get("context key1"));
        assertEquals("context value2", myTab.get("context key2"));
    }

    /**
     * Gets a hashmap key from the meta data in a notification
     *
     * @param notification The notification
     * @param key The key to get
     * @return The hash map
     */
    @SuppressWarnings (value = "unchecked")
    private Map<String, Object> getFilteredMetaDataMap(Notification notification, String key) {
        return ((Map<String, Object>) notification.getEvents().get(0).getFilteredMetaData().get(key));
    }

    /**
     * Gets a hashmap key from the meta data in a notification
     *
     * @param notification The notification
     * @param key The key to get
     * @return The hash map
     */
    @SuppressWarnings (value = "unchecked")
    private Map<String, Object> getRedactedMetaDataMap(Notification notification, String key) {
        return ((Map<String, Object>) notification.getEvents().get(0).getRedactedMetaData().get(key));
    }
}
