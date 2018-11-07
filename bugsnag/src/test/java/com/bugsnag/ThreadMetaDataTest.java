package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.bugsnag.delivery.Delivery;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class ThreadMetaDataTest {

    private StubNotificationDelivery delivery;
    private Delivery originalDelivery;
    private Bugsnag bugsnag;

    /**
     * Create a new test delivery and assign it to the Bugsnag client
     */
    @Before
    public void swapDelivery() {
        bugsnag = Bugsnag.init("testapikey");
        originalDelivery = bugsnag.getDelivery();
        delivery = new StubNotificationDelivery();
        bugsnag.setDelivery(delivery);
    }

    /**
     * Restore the previous delivery objects after the test
     */
    @After
    public void revertDelivery() {
        bugsnag.setDelivery(originalDelivery);
        bugsnag.close();
    }

    @Test
    public void testMetaDataClearAll() {

        // Add some thread meta data
        Bugsnag.addThreadMetaData("thread", "some key", "some thread value");

        bugsnag.notify(new RuntimeException("test"));
        Bugsnag.clearThreadMetaData();
        bugsnag.notify(new RuntimeException("test"));

        // Check that two reports were sent to Bugsnag
        assertEquals(2, delivery.getNotifications().size());

        // Check the meta data is added to the first report
        Notification notification = delivery.getNotifications().get(0);
        Report report = notification.getEvents().get(0);
        assertTrue(report.getMetaData().containsKey("thread"));
        assertEquals("some thread value", getMetaDataMap(notification, "thread").get("some key"));

        // Check the meta data is not added to the second report
        notification = delivery.getNotifications().get(1);
        report = notification.getEvents().get(0);
        assertFalse(report.getMetaData().containsKey("thread"));
    }

    @Test
    public void testMetaDataClearTab() {

        // Add some thread meta data
        Bugsnag.addThreadMetaData("tab1", "some key", "some value");
        Bugsnag.addThreadMetaData("tab2", "some key", "some value");

        bugsnag.notify(new RuntimeException("test"));
        Bugsnag.clearThreadMetaData("tab2");
        bugsnag.notify(new RuntimeException("test"));

        // Check that two reports were sent to Bugsnag
        assertEquals(2, delivery.getNotifications().size());

        // Check that both tabs are populated in the first report
        Notification notification = delivery.getNotifications().get(0);
        Report report = notification.getEvents().get(0);
        assertTrue(report.getMetaData().containsKey("tab1"));
        assertEquals("some value", getMetaDataMap(notification, "tab1").get("some key"));
        assertTrue(report.getMetaData().containsKey("tab2"));
        assertEquals("some value", getMetaDataMap(notification, "tab2").get("some key"));

        // Check that only the first tab is in the second tab
        notification = delivery.getNotifications().get(1);
        report = notification.getEvents().get(0);
        assertTrue(report.getMetaData().containsKey("tab1"));
        assertEquals("some value", getMetaDataMap(notification, "tab1").get("some key"));
        assertFalse(report.getMetaData().containsKey("tab2"));
    }

    @Test
    public void testMetaDataClearKey() {

        // Add some thread meta data
        Bugsnag.addThreadMetaData("tab1", "key1", "some value");
        Bugsnag.addThreadMetaData("tab1", "key2", "some value");

        bugsnag.notify(new RuntimeException("test"));
        Bugsnag.clearThreadMetaData("tab1", "key2");
        bugsnag.notify(new RuntimeException("test"));

        // Check that two reports were sent to Bugsnag
        assertEquals(2, delivery.getNotifications().size());

        // Check that both keys are populated in the first report
        Notification notification = delivery.getNotifications().get(0);
        Report report = notification.getEvents().get(0);
        assertTrue(report.getMetaData().containsKey("tab1"));
        assertEquals("some value", getMetaDataMap(notification, "tab1").get("key1"));
        assertEquals("some value", getMetaDataMap(notification, "tab1").get("key2"));

        // Check that only the first tab is in the second tab
        notification = delivery.getNotifications().get(1);
        report = notification.getEvents().get(0);
        assertTrue(report.getMetaData().containsKey("tab1"));
        assertEquals("some value", getMetaDataMap(notification, "tab1").get("key1"));
        assertFalse(getMetaDataMap(notification, "tab1").containsKey("key2"));
    }

    @Test
    public void testInnerThreadMetaData() {

        // Add some thread meta data in the outer thread
        Bugsnag.addThreadMetaData("outerthread", "some key", "value should not be in report");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Add thread meta data which should get associated with the exception
                Bugsnag.addThreadMetaData("innerthread", "some key", "value should be in report");

                // Notify to Bugsnag
                bugsnag.notify(new RuntimeException("test"));
            }
        });
        thread.start();

        // Wait for thread to run
        try {
            thread.join();
        } catch (InterruptedException ex) {
            // ignore
        }

        // Check that the data was included in the notification
        Notification notification = delivery.getNotifications().get(0);
        Report report = notification.getEvents().get(0);

        assertTrue(report.getMetaData().containsKey("innerthread"));
        assertEquals("value should be in report",
                getMetaDataMap(notification, "innerthread").get("some key"));

        assertFalse(report.getMetaData().containsKey("outerthread"));
    }

    @Test
    public void testUnhandledThreadMetaDataRemoval() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Add some thread meta data
                Bugsnag.addThreadMetaData("thread", "key1", "should be cleared from meta data");

                Bugsnag.clearThreadMetaData();

                // Add some thread meta data
                Bugsnag.addThreadMetaData("thread", "key2", "should be included in meta data");

                // Thrown unhandled exception
                throw new RuntimeException("test");
            }
        });
        thread.start();

        // Wait for thread to run
        try {
            thread.join();
        } catch (InterruptedException ex) {
            // ignore
        }

        // Check that the data was included in the notification
        Notification notification = delivery.getNotifications().get(0);
        Report report = notification.getEvents().get(0);

        assertTrue(report.getMetaData().containsKey("thread"));
        assertFalse(getMetaDataMap(notification, "thread").containsKey("key1"));
        assertEquals("should be included in meta data",
                getMetaDataMap(notification, "thread").get("key2"));
    }

    @Test
    public void testUnhandledThreadMetaData() {

        // Add some thread meta data in the outer thread
        Bugsnag.addThreadMetaData("outerthread", "some key", "value should not be in report");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Add thread meta data which should get associated with the exception
                Bugsnag.addThreadMetaData("innerthread", "some key", "value should be in report");

                // Thrown unhandled exception
                throw new RuntimeException("test");
            }
        });
        thread.start();

        // Wait for thread to run
        try {
            thread.join();
        } catch (InterruptedException ex) {
            // ignore
        }

        // Check that the data was included in the notification
        Notification notification = delivery.getNotifications().get(0);
        Report report = notification.getEvents().get(0);

        assertTrue(report.getMetaData().containsKey("innerthread"));
        assertEquals("value should be in report",
                getMetaDataMap(notification, "innerthread").get("some key"));

        assertFalse(report.getMetaData().containsKey("outerthread"));
    }

    /**
     * Gets a hashmap key from the meta data in a notification
     *
     * @param notification The notification
     * @param key The key to get
     * @return The hash map
     */
    @SuppressWarnings (value = "unchecked")
    private Map<String, Object> getMetaDataMap(Notification notification, String key) {
        return ((Map<String, Object>) notification.getEvents().get(0).getMetaData().get(key));
    }
}
