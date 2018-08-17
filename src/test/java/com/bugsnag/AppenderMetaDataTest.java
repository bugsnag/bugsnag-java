package com.bugsnag;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import com.bugsnag.delivery.Delivery;

import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test for using meta data via the Bugsnag Appender
 */
public class AppenderMetaDataTest {

    private static final Logger LOGGER = Logger.getLogger(AppenderMetaDataTest.class);
    private static StubNotificationDelivery delivery;
    private static Delivery originalDelivery;

    /**
     * Create a new test delivery and assign it to the Bugsnag client
     */
    @Before
    public void swapDelivery() {
        Bugsnag bugsnag = Bugsnag.init("appenderApikey");
        originalDelivery = bugsnag.getDelivery();
        delivery = new StubNotificationDelivery();
        bugsnag.setDelivery(delivery);
    }

    /**
     * Restore the previous delivery objects after the test
     */
    @After
    public void revertDelivery() {
        Bugsnag bugsnag = Bugsnag.init("appenderApikey");
        bugsnag.setDelivery(originalDelivery);
    }

    @Test
    public void testMetaDataFromLogbackFile() {

        // Send a log message
        LOGGER.warn("Test exception", new RuntimeException("test"));

        // Get the notification details
        Notification notification = delivery.getNotifications().get(0);
        assertTrue(notification.getEvents().get(0).getMetaData().containsKey("logbackTab"));
        Map<String, Object> myTab = getMetaDataMap(notification, "logbackTab");

        assertEquals("logbackValue1", myTab.get("logbackKey1"));
        assertEquals("logbackValue2", myTab.get("logbackKey2"));
    }

    @Test
    public void testMetaDataTypes() {

        BugsnagAppender.addReportMetaData("myTab", "string key", "string value");
        BugsnagAppender.addReportMetaData("myTab", "bool key", true);
        BugsnagAppender.addReportMetaData("myTab", "int key", 1);
        BugsnagAppender.addReportMetaData("myTab", "float key", 1.1);

        Map<String, String> map = new HashMap<String, String>();
        map.put("key", "value");
        BugsnagAppender.addReportMetaData("myTab", "object key", map);

        Integer[] array = new Integer[] {1,2,3,4,5};
        BugsnagAppender.addReportMetaData("myTab", "array key", array);

        // Send a log message
        LOGGER.warn("Test exception", new RuntimeException("test"));

        // Get the notification details
        Notification notification = delivery.getNotifications().get(0);
        assertTrue(notification.getEvents().get(0).getMetaData().containsKey("myTab"));
        Map<String, Object> myTab = getMetaDataMap(notification, "myTab");

        assertEquals("string value", myTab.get("string key"));
        assertEquals("true", myTab.get("bool key"));
        assertEquals("1", myTab.get("int key"));
        assertEquals("1.1", myTab.get("float key"));
        assertEquals(map, myTab.get("object key"));
        assertThat((List<Integer>)myTab.get("array key"), is(Arrays.asList(array)));
    }


    @Test
    public void testMetaDataRemoval() {

        // Add some report and some thread meta data
        BugsnagAppender.addReportMetaData("report", "some key", "some report value");
        BugsnagAppender.addThreadMetaData("thread", "some key", "some thread value");

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
        assertEquals("some report value", getMetaDataMap(notification, "report").get("some key"));
        assertTrue(notification.getEvents().get(0).getMetaData().containsKey("thread"));
        assertEquals("some thread value", getMetaDataMap(notification, "thread").get("some key"));

        // Should have just thread meta data
        notification = delivery.getNotifications().get(1);
        assertFalse(notification.getEvents().get(0).getMetaData().containsKey("report"));
        assertTrue(notification.getEvents().get(0).getMetaData().containsKey("thread"));
        assertEquals("some thread value", getMetaDataMap(notification, "thread").get("some key"));

        // Should have neither meta data
        notification = delivery.getNotifications().get(2);
        assertFalse(notification.getEvents().get(0).getMetaData().containsKey("report"));
        assertFalse(notification.getEvents().get(0).getMetaData().containsKey("thread"));
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
}
