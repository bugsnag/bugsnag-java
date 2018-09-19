package com.bugsnag;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.bugsnag.delivery.Delivery;
import com.bugsnag.serialization.Serializer;

import org.mockito.ArgumentCaptor;

class TestUtils {

    /**
     * Verify that a report was received, then capture and return that report
     */
    static Report verifyAndGetReport(Delivery delivery) {
        ArgumentCaptor<Notification> notificationCaptor =
                ArgumentCaptor.forClass(Notification.class);
        verify(delivery, times(1)).deliver(
                any(Serializer.class),
                notificationCaptor.capture(),
                anyMapOf(String.class, String.class));
        return notificationCaptor.getValue().getEvents().get(0);
    }
}
