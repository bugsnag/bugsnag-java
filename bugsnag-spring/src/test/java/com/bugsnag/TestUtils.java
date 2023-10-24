package com.bugsnag;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

import com.bugsnag.delivery.Delivery;
import com.bugsnag.serialization.Serializer;

import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;

import java.util.Map;

class TestUtils {

    /**
     * Verify that a report was received, then capture and return that report
     */
    static Report verifyAndGetReport(Delivery delivery) {
        ArgumentCaptor<Notification> notificationCaptor =
                ArgumentCaptor.forClass(Notification.class);
        verify(delivery, timeout(100).times(1)).deliver(
                any(Serializer.class),
                notificationCaptor.capture(),
                anyMapOf(String.class, String.class));
        return notificationCaptor.getValue().getEvents().get(0);
    }

    static <K, V> Map<K, V> anyMapOf(Class<K> keyClazz, Class<V> valueClazz) {
        return ArgumentMatchers.anyMap();
    }
}
