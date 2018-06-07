package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.bugsnag.delivery.Delivery;
import com.bugsnag.delivery.HttpDelivery;
import com.bugsnag.serialization.Serializer;

import org.junit.Before;
import org.junit.Test;

import java.net.Proxy;
import java.util.Map;

public class ConfigurationTest {

    private Configuration config;

    /**
     * Creates a config object with fake delivery objects
     *
     * @throws Throwable if setup failed
     */
    @Before
    public void setUp() throws Throwable {
        config = new Configuration("foo");
        config.delivery = new FakeHttpDelivery();
        config.sessionDelivery = new FakeHttpDelivery();
    }

    @Test
    public void testDefaults() throws java.lang.Exception {
        assertTrue(config.shouldAutoCaptureSessions());
    }

    @Test
    public void testErrorApiHeaders() throws java.lang.Exception {
        Map<String, String> headers = config.getErrorApiHeaders();
        assertEquals(config.apiKey, headers.get("Bugsnag-Api-Key"));
        assertNotNull(headers.get("Bugsnag-Sent-At"));
        assertNotNull(headers.get("Bugsnag-Payload-Version"));
    }

    @Test
    public void testSessionApiHeaders() throws java.lang.Exception {
        Map<String, String> headers = config.getSessionApiHeaders();
        assertEquals(config.apiKey, headers.get("Bugsnag-Api-Key"));
        assertNotNull(headers.get("Bugsnag-Sent-At"));
        assertNotNull(headers.get("Bugsnag-Payload-Version"));
    }

    @Test
    public void testEndpoints() {
        String notify = "https://notify.myexample.com";
        String sessions = "https://sessions.myexample.com";
        config.setEndpoints(notify, sessions);

        assertEquals(notify, getDeliveryEndpoint(config.delivery));
        assertEquals(sessions, getDeliveryEndpoint(config.sessionDelivery));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullNotifyEndpoint() {
        config.setEndpoints(null, "http://example.com");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyNotifyEndpoint() {
        config.setEndpoints("", "http://example.com");
    }

    @Test
    public void testInvalidSessionEndpoint() {
        config.setEndpoints("http://example.com", null);
        assertFalse(config.shouldAutoCaptureSessions());
        assertNull(getDeliveryEndpoint(config.sessionDelivery));

        config.setEndpoints("http://example.com", "");
        assertFalse(config.shouldAutoCaptureSessions());
        assertNull(getDeliveryEndpoint(config.sessionDelivery));

        config.setEndpoints("http://example.com", "http://sessions.example.com");
        assertFalse(config.shouldAutoCaptureSessions());
        assertEquals("http://sessions.example.com", getDeliveryEndpoint(config.sessionDelivery));
    }

    @Test
    public void testAutoCaptureOverride() {
        config.setAutoCaptureSessions(false);
        config.setEndpoints("http://example.com", "http://example.com");
        assertFalse(config.shouldAutoCaptureSessions());
    }

    private String getDeliveryEndpoint(Delivery delivery) {
        if (delivery instanceof FakeHttpDelivery) {
            return ((FakeHttpDelivery) delivery).endpoint;
        }
        return null;
    }

    static class FakeHttpDelivery implements HttpDelivery {
        String endpoint;

        @Override
        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        @Override
        public void setTimeout(int timeout) {
        }

        @Override
        public void setProxy(Proxy proxy) {
        }

        @Override
        public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
        }

        @Override
        public void close() {
        }
    }
}
