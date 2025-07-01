package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.bugsnag.delivery.Delivery;
import com.bugsnag.delivery.HttpDelivery;
import com.bugsnag.serialization.DefaultSerializer;
import com.bugsnag.serialization.SerializationException;
import com.bugsnag.serialization.Serializer;

import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Proxy;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class ConfigurationTest {

    private static final String HUB_KEY = "00000aaaaaaaaaaaaaaaaaaaaaaaaaaa";
    private static final String CLASSIC_KEY = "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb";
    private Configuration config;

    /**
     * Creates a config object with fake delivery objects
     *
     * @throws Throwable if setup failed
     */
    @Before
    public void setUp() {
        config = new Configuration("foo");
        config.delivery = new FakeHttpDelivery();
        config.sessionDelivery = new FakeHttpDelivery();
    }

    @Test
    public void testDefaults() {
        assertTrue(config.shouldAutoCaptureSessions());
    }

    @Test
    public void testErrorApiHeaders() {
        Map<String, String> headers = config.getErrorApiHeaders();
        assertEquals(config.apiKey, headers.get("Bugsnag-Api-Key"));
        assertNotNull(headers.get("Bugsnag-Sent-At"));
        assertNotNull(headers.get("Bugsnag-Payload-Version"));
    }

    @Test
    public void testSessionApiHeaders() {
        Map<String, String> headers = config.getSessionApiHeaders();
        assertEquals(config.apiKey, headers.get("Bugsnag-Api-Key"));
        assertNotNull(headers.get("Bugsnag-Sent-At"));
        assertNotNull(headers.get("Bugsnag-Payload-Version"));
    }

    @Test
    public void testEndpoints() {
        String notify = "https://notify.myexample.com";
        String sessions = "https://sessions.myexample.com";
        config.setEndpoints(new EndpointConfiguration(notify, sessions));

        assertEquals(notify, getDeliveryEndpoint(config.delivery));
        assertEquals(sessions, getDeliveryEndpoint(config.sessionDelivery));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullNotifyEndpoint() {
        config.setEndpoints(new EndpointConfiguration(null, "http://example.com"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyNotifyEndpoint() {
        config.setEndpoints(new EndpointConfiguration("", "http://example.com"));
    }

    @Test
    public void testInvalidSessionEndpoint() {
        config.setAutoCaptureSessions(true);
        EndpointConfiguration emptySession = new EndpointConfiguration("http://example.com", "");
        config.setEndpoints(emptySession);
        assertFalse(config.shouldAutoCaptureSessions());
        assertNull(getDeliveryEndpoint(config.sessionDelivery));

        config.setAutoCaptureSessions(true);
        EndpointConfiguration validSessions = new EndpointConfiguration(
                "http://example.com", "http://sessions.example.com");
        config.setEndpoints(validSessions);
        assertTrue(config.shouldAutoCaptureSessions());
        assertEquals("http://sessions.example.com", getDeliveryEndpoint(config.sessionDelivery));
    }

    @Test
    public void testInvalidSessionWarningLogged() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setErr(new PrintStream(baos));
        config.setEndpoints(new EndpointConfiguration("http://example.com", ""));
        String logMsg = new String(baos.toByteArray());
        assertTrue(logMsg.contains("The session tracking endpoint "
                + "has not been set. Session tracking is disabled"));
    }

    @Test
    public void testAutoCaptureOverride() {
        config.setAutoCaptureSessions(false);
        config.setEndpoints(new EndpointConfiguration("http://example.com", "http://example.com"));
        assertFalse(config.shouldAutoCaptureSessions());
    }

    @Test
    public void testCustomSerializer() throws SerializationException {
        // flag to check if writeToStream was called
        final boolean[] methodCalled = {false};

        //Anonymous class extending DefaultSerializer
        Serializer customSerializer = new DefaultSerializer() {
            @Override
            public void writeToStream(OutputStream stream, Object object) throws SerializationException {
                methodCalled[0] = true;
                try {
                    stream.write("foo".getBytes());
                } catch (IOException exc) {
                    throw new SerializationException("Exception during serialization", exc);
                }
            }
        };
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        customSerializer.writeToStream(out, new Object());
        assertTrue(methodCalled[0]);
        assertEquals("foo", out.toString());
    }

    @Test
    public void testBaseDeliveryIgnoresEndpoint() {
        Delivery delivery = new Delivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
            }

            @Override
            public void close() {
            }
        };
        config.delivery = delivery;
        config.sessionDelivery = delivery;

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        System.setErr(new PrintStream(baos));
        config.setEndpoints(new EndpointConfiguration("http://example.com", "http://sessions.example.com"));
        String logMsg = new String(baos.toByteArray());

        assertTrue(logMsg.contains("Delivery is not instance of "
                + "HttpDelivery, cannot set notify endpoint"));
        assertTrue(logMsg.contains("Delivery is not instance of "
                + "HttpDelivery, cannot set sessions endpoint"));
    }

    @Test
    public void testStaticEndpointHelpers() {

        String DEFAULT_NOTIFY_ENDPOINT = "https://notify.bugsnag.com";
        String DEFAULT_SESSION_ENDPOINT = "https://sessions.bugsnag.com";
        String HUB_NOTIFY_ENDPOINT = "https://notify.insighthub.smartbear.com";
        String HUB_SESSION_ENDPOINT = "https://sessions.insighthub.smartbear.com";

        EndpointConfiguration normalConfig = new EndpointConfiguration();
        normalConfig.configureDefaultEndpoints(CLASSIC_KEY);

        assertEquals(DEFAULT_NOTIFY_ENDPOINT,
                normalConfig.notifyEndpoint);
        assertEquals(DEFAULT_SESSION_ENDPOINT,
                normalConfig.sessionEndpoint);

        EndpointConfiguration hubConfig = new EndpointConfiguration();
        hubConfig.configureDefaultEndpoints(HUB_KEY);

        assertEquals(HUB_NOTIFY_ENDPOINT,
                hubConfig.notifyEndpoint);
        assertEquals(HUB_SESSION_ENDPOINT,
                hubConfig.sessionEndpoint);

    }

    private String getDeliveryEndpoint(Delivery delivery) {
        if (delivery instanceof FakeHttpDelivery) {
            return ((FakeHttpDelivery) delivery).endpoint;
        }
        return null;
    }

    static class FakeHttpDelivery implements HttpDelivery {
        String endpoint;
        Queue<Object> receivedObjects = new LinkedList<Object>();

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
            receivedObjects.add(object);
        }

        @Override
        public void close() {
        }
    }
}
