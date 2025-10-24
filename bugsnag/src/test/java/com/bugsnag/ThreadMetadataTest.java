package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.bugsnag.delivery.Delivery;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class ThreadMetadataTest {

    private StubNotificationDelivery delivery;
    private Delivery originalDelivery;
    private Bugsnag bugsnag;

    /**
     * Create a new test delivery and assign it to the Bugsnag client
     */
    @Before
    public void swapDelivery() {
        bugsnag = new Bugsnag("testapikey");
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
    public void testMetadataClearAll() {

        // Add some thread metadata
        Bugsnag.addThreadMetadata("thread", "some key", "some thread value");

        bugsnag.notify(new RuntimeException("test"));
        Bugsnag.clearThreadMetadata();
        bugsnag.notify(new RuntimeException("test"));

        // Check that two reports were sent to Bugsnag
        assertEquals(2, delivery.getNotifications().size());

        // Check the metadata is added to the first report
        Notification notification = delivery.getNotifications().get(0);
        Report report = notification.getEvents().get(0);
        assertTrue(report.getMetadata().containsKey("thread"));
        assertEquals("some thread value", getMetadataMap(notification, "thread").get("some key"));

        // Check the metadata is not added to the second report
        notification = delivery.getNotifications().get(1);
        report = notification.getEvents().get(0);
        assertFalse(report.getMetadata().containsKey("thread"));
    }

    @Test
    public void testMetadataClearTab() {

        // Add some thread metadata
        Bugsnag.addThreadMetadata("tab1", "some key", "some value");
        Bugsnag.addThreadMetadata("tab2", "some key", "some value");

        bugsnag.notify(new RuntimeException("test"));
        Bugsnag.clearThreadMetadata("tab2");
        bugsnag.notify(new RuntimeException("test"));

        // Check that two reports were sent to Bugsnag
        assertEquals(2, delivery.getNotifications().size());

        // Check that both tabs are populated in the first report
        Notification notification = delivery.getNotifications().get(0);
        Report report = notification.getEvents().get(0);
        assertTrue(report.getMetadata().containsKey("tab1"));
        assertEquals("some value", getMetadataMap(notification, "tab1").get("some key"));
        assertTrue(report.getMetadata().containsKey("tab2"));
        assertEquals("some value", getMetadataMap(notification, "tab2").get("some key"));

        // Check that only the first tab is in the second tab
        notification = delivery.getNotifications().get(1);
        report = notification.getEvents().get(0);
        assertTrue(report.getMetadata().containsKey("tab1"));
        assertEquals("some value", getMetadataMap(notification, "tab1").get("some key"));
        assertFalse(report.getMetadata().containsKey("tab2"));
    }

    @Test
    public void testMetadataClearKey() {

        // Add some thread metadata
        Bugsnag.addThreadMetadata("tab1", "key1", "some value");
        Bugsnag.addThreadMetadata("tab1", "key2", "some value");

        bugsnag.notify(new RuntimeException("test"));
        Bugsnag.clearThreadMetadata("tab1", "key2");
        bugsnag.notify(new RuntimeException("test"));

        // Check that two reports were sent to Bugsnag
        assertEquals(2, delivery.getNotifications().size());

        // Check that both keys are populated in the first report
        Notification notification = delivery.getNotifications().get(0);
        Report report = notification.getEvents().get(0);
        assertTrue(report.getMetadata().containsKey("tab1"));
        assertEquals("some value", getMetadataMap(notification, "tab1").get("key1"));
        assertEquals("some value", getMetadataMap(notification, "tab1").get("key2"));

        // Check that only the first tab is in the second tab
        notification = delivery.getNotifications().get(1);
        report = notification.getEvents().get(0);
        assertTrue(report.getMetadata().containsKey("tab1"));
        assertEquals("some value", getMetadataMap(notification, "tab1").get("key1"));
        assertFalse(getMetadataMap(notification, "tab1").containsKey("key2"));
    }

    @Test
    public void testInnerThreadMetadata() {

        // Add some thread metadata in the outer thread
        Bugsnag.addThreadMetadata("outerthread", "some key", "value should not be in report");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Add thread metadata which should get associated with the exception
                Bugsnag.addThreadMetadata("innerthread", "some key", "value should be in report");

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

        assertTrue(report.getMetadata().containsKey("innerthread"));
        assertEquals("value should be in report",
                getMetadataMap(notification, "innerthread").get("some key"));

        assertFalse(report.getMetadata().containsKey("outerthread"));
    }

    @Test
    public void testUnhandledThreadMetadataRemoval() {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Add some thread metadata
                Bugsnag.addThreadMetadata("thread", "key1", "should be cleared from metadata");

                Bugsnag.clearThreadMetadata();

                // Add some thread metadata
                Bugsnag.addThreadMetadata("thread", "key2", "should be included in metadata");

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

        assertTrue(report.getMetadata().containsKey("thread"));
        assertFalse(getMetadataMap(notification, "thread").containsKey("key1"));
        assertEquals("should be included in metadata",
                getMetadataMap(notification, "thread").get("key2"));
    }

    @Test
    public void testUnhandledThreadMetadata() {

        // Add some thread metadata in the outer thread
        Bugsnag.addThreadMetadata("outerthread", "some key", "value should not be in report");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // Add thread metadata which should get associated with the exception
                Bugsnag.addThreadMetadata("innerthread", "some key", "value should be in report");

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

        assertTrue(report.getMetadata().containsKey("innerthread"));
        assertEquals("value should be in report",
                getMetadataMap(notification, "innerthread").get("some key"));

        assertFalse(report.getMetadata().containsKey("outerthread"));
    }

    /**
     * Gets a hashmap key from the metadata in a notification
     *
     * @param notification The notification
     * @param key The key to get
     * @return The hash map
     */
    @SuppressWarnings (value = "unchecked")
    private Map<String, Object> getMetadataMap(Notification notification, String key) {
        return ((Map<String, Object>) notification.getEvents().get(0).getMetadata().get(key));
    }
}
