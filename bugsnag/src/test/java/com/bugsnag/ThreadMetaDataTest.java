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

    private static StubNotificationDelivery delivery;
    private static Delivery originalDelivery;
    private static Bugsnag bugsnag;

    /**
     * Create a new test delivery and assign it to the Bugsnag client
     */
    @Before
    public void swapDelivery() {
        bugsnag = Bugsnag.init("apikey");
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
    }

    @Test
    public void testMetaDataRemoval() {

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
            Thread.sleep(2000);
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
            Thread.sleep(2000);
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
            Thread.sleep(2000);
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
