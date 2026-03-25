package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CallbackSuppressionTest {

    private Bugsnag bugsnag;
    private StubNotificationDelivery delivery;

    /**
     * Set up test fixtures.
     */
    @Before
    public void setUp() {
        bugsnag = new Bugsnag("apikey");
        delivery = new StubNotificationDelivery();
        bugsnag.setDelivery(delivery);
    }

    @After
    public void tearDown() {
        bugsnag.close();
    }

    @Test
    public void callbackReturningFalseSuppressesDelivery() {
        bugsnag.addOnError(report -> false); // explicit suppression

        boolean result = bugsnag.notify(new RuntimeException("Suppressed"));
        assertFalse("notify should return false when suppressed", result);
        assertEquals("No notifications should be delivered", 0, delivery.getNotifications().size());
    }

    @Test
    public void callbackReturningTrueAllowsDelivery() {
        bugsnag.addOnError(report -> true); // allow

        boolean result = bugsnag.notify(new RuntimeException("Allowed"));
        assertTrue(result);
        assertEquals(1, delivery.getNotifications().size());
    }
}
