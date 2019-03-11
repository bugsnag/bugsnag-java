package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.bugsnag.delivery.Delivery;
import com.bugsnag.serialization.Serializer;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;


public class SessionTrackerTest {

    private SessionTracker sessionTracker;
    private Configuration configuration;
    private ConfigurationTest.FakeHttpDelivery sessionDelivery;

    /**
     * Initialises config + session tracker
     *
     * @throws Throwable the throwable
     */
    @Before
    public void setUp() {
        configuration = new Configuration("api-key");
        sessionDelivery = new ConfigurationTest.FakeHttpDelivery();
        configuration.setSessionDelivery(sessionDelivery);
        sessionTracker = new SessionTracker(configuration);
        assertNull(sessionTracker.getSession());
    }

    @Test
    public void startManualSessionAutoEnabled() {
        sessionTracker.startSession(new Date(), false);
        assertNotNull(sessionTracker.getSession());
    }

    @Test
    public void startManualSessionAutoDisabled() {
        configuration.setAutoCaptureSessions(false);
        sessionTracker.startSession(new Date(), false);
        assertNotNull(sessionTracker.getSession());
    }

    @Test
    public void startAutoSessionAutoEnabled() {
        sessionTracker.startSession(new Date(), true);
        assertNotNull(sessionTracker.getSession());
    }

    @Test
    public void startAutoSessionAutoDisabled() {
        configuration.setAutoCaptureSessions(false);
        sessionTracker.startSession(new Date(), true);
        assertNull(sessionTracker.getSession());
    }

    @Test
    public void startSessionNoEndpoint() {
        configuration.setEndpoints("http://example.com", null);
        sessionTracker.startSession(new Date(), true);
        assertNull(sessionTracker.getSession());
    }

    @Test
    public void testMultiSessionsOneBatch() {
        for (int k = 0; k < 100; k++) {
            sessionTracker.startSession(new Date(k), false);
        }
        sessionTracker.flushSessions(new Date());
        assertEquals(1, sessionDelivery.getReceivedObjects().size());
        SessionPayload payload = (SessionPayload) sessionDelivery.getReceivedObjects().poll();

        List<SessionCount> sessionCounts = (List<SessionCount>) payload.getSessionCounts();
        assertEquals(1, sessionCounts.size());
        assertEquals(100, sessionCounts.get(0).getSessionsStarted());
    }

    @Test
    public void testMultiSessionsTwoBatches() {
        for (int k = 0; k < 100; k++) {
            sessionTracker.startSession(new Date(k * 1000), false);
        }
        sessionTracker.flushSessions(new Date());
        assertEquals(1, sessionDelivery.getReceivedObjects().size());
        SessionPayload payload = (SessionPayload) sessionDelivery.getReceivedObjects().poll();

        List<SessionCount> sessionCounts = (List<SessionCount>) payload.getSessionCounts();
        assertEquals(2, sessionCounts.size());
        assertEquals(60, sessionCounts.get(0).getSessionsStarted());
        assertEquals(40, sessionCounts.get(1).getSessionsStarted());
    }

    @Test
    public void startTwoSessionsSameThread() {
        sessionTracker.startSession(new Date(), false);
        Session first = sessionTracker.getSession();

        sessionTracker.startSession(new Date(), false);
        Session second = sessionTracker.getSession();
        assertNotEquals(first, second);
    }

    @Test
    public void startTwoSessionsDiffThread() throws Throwable {
        sessionTracker.startSession(new Date(), false);
        final Session first = sessionTracker.getSession();
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                sessionTracker.startSession(new Date(), false);
                countDownLatch.countDown();
            }
        }).start();

        countDownLatch.await();
        assertEquals(first, sessionTracker.getSession());
    }

    @Test
    public void disabledReleaseStage() {
        configuration.setNotifyReleaseStages(new String[]{"prod"});
        configuration.setReleaseStage("dev");
        sessionTracker.startSession(new Date(), false);
        assertNull(sessionTracker.getSession());
    }

    @Test
    public void enabledReleaseStage() {
        configuration.setNotifyReleaseStages(new String[]{"prod"});
        configuration.setReleaseStage("prod");
        sessionTracker.startSession(new Date(), false);
        assertNotNull(sessionTracker.getSession());
    }

    @Test
    public void zeroSessionDelivery() {
        CustomDelivery sessionDelivery = new CustomDelivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                super.deliver(serializer, object, headers);
                fail("Should not be called if no sessions enqueued");
            }
        };
        configuration.setSessionDelivery(sessionDelivery);
        sessionTracker.flushSessions(new Date());
        assertFalse(sessionDelivery.delivered);
    }

    @Test
    public void noDateChangeSessionDelivery() {
        CustomDelivery sessionDelivery = new CustomDelivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                super.deliver(serializer, object, headers);
                fail("Should not be called if date has not exceeded batch period");
            }
        };
        configuration.setSessionDelivery(sessionDelivery);
        sessionTracker.startSession(new Date(1309209859), false);
        sessionTracker.flushSessions(new Date(1309209859));
        assertFalse(sessionDelivery.delivered);
    }

    @Test
    public void multiSessionDelivery() {
        CustomDelivery sessionDelivery = new CustomDelivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                super.deliver(serializer, object, headers);
                SessionPayload payload = (SessionPayload) object;


                List<SessionCount> sessionCounts = (List<SessionCount>) payload.getSessionCounts();
                assertEquals(3, sessionCounts.size());

                SessionCount sessionCount = sessionCounts.get(0);
                assertEquals(1, sessionCount.getSessionsStarted());
                assertEquals("1970-01-01T01:24:00Z", sessionCount.getStartedAt());

                sessionCount = sessionCounts.get(1);
                assertEquals(1, sessionCount.getSessionsStarted());
                assertEquals("1970-01-02T10:44:00Z", sessionCount.getStartedAt());

                sessionCount = sessionCounts.get(2);
                assertEquals(2, sessionCount.getSessionsStarted());
                assertEquals("1970-01-18T11:13:00Z", sessionCount.getStartedAt());
            }
        };
        configuration.setSessionDelivery(sessionDelivery);
        sessionTracker.startSession(new Date(5092340L), false);
        sessionTracker.startSession(new Date(125098234L), false);
        sessionTracker.startSession(new Date(1509207501L), false);
        sessionTracker.startSession(new Date(1509209834L), false);
        sessionTracker.flushSessions(new Date(1609209834L));
        assertTrue(sessionDelivery.delivered);
    }

    @Test
    public void sessionDeliveryDiffMin() {
        CustomDelivery sessionDelivery = new CustomDelivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                super.deliver(serializer, object, headers);
                SessionPayload payload = (SessionPayload) object;

                List<SessionCount> sessionCounts = (List<SessionCount>) payload.getSessionCounts();
                assertEquals(1, sessionCounts.size());
                SessionCount sessionCount = sessionCounts.get(0);
                assertEquals(1, sessionCount.getSessionsStarted());
                assertEquals("1970-01-01T02:46:00Z", sessionCount.getStartedAt());
            }
        };
        configuration.setSessionDelivery(sessionDelivery);

        // 2 mins apart
        sessionTracker.startSession(new Date(10000000L), false);
        sessionTracker.flushSessions(new Date(10120000L));
        assertTrue(sessionDelivery.delivered);
    }

    @Test
    public void sessionDeliverySameMin() {
        CustomDelivery sessionDelivery = new CustomDelivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                super.deliver(serializer, object, headers);
                SessionPayload payload = (SessionPayload) object;

                List<SessionCount> sessionCounts = (List<SessionCount>) payload.getSessionCounts();
                assertEquals(1, sessionCounts.size());
                SessionCount sessionCount = sessionCounts.get(0);
                assertEquals(1, sessionCount.getSessionsStarted());
                assertEquals("1970-01-01T02:46:00Z", sessionCount.getStartedAt());
            }
        };
        configuration.setSessionDelivery(sessionDelivery);

        // 1 hour apart
        sessionTracker.startSession(new Date(10000000L), false);
        sessionTracker.flushSessions(new Date(13600000L));
        assertTrue(sessionDelivery.delivered);
    }

    @Test
    public void sessionDeliveryMultiFlush() {
        CustomDelivery sessionDelivery = new CustomDelivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                super.deliver(serializer, object, headers);
                SessionPayload payload = (SessionPayload) object;

                List<SessionCount> sessionCounts = (List<SessionCount>) payload.getSessionCounts();
                assertEquals(1, sessionCounts.size());
                SessionCount sessionCount = sessionCounts.get(0);
                assertEquals(1, sessionCount.getSessionsStarted());
                assertEquals("1970-01-01T02:46:00Z", sessionCount.getStartedAt());
            }
        };
        configuration.setSessionDelivery(sessionDelivery);

        // 1 hour apart
        sessionTracker.startSession(new Date(10000000L), false);
        sessionTracker.flushSessions(new Date(13600000L));
        sessionTracker.flushSessions(new Date(13600000L));
        assertTrue(sessionDelivery.delivered);
        assertEquals(1, sessionDelivery.count.get());
    }

    @Test
    public void zeroSessionCount() {
        CustomDelivery sessionDelivery = new CustomDelivery() {};
        configuration.setSessionDelivery(sessionDelivery);
        sessionTracker.flushSessions(new Date(10120000L));
        sessionTracker.flushSessions(new Date(14000000L));
        assertFalse(sessionDelivery.delivered);
    }

    @Test
    public void testSessionShutdownStartSession() {
        sessionTracker.shutdown();
        sessionTracker.startSession(new Date(), true);
        assertNull(sessionTracker.getSession());
    }

    @Test
    public void testSessionShutdownDelivers() {
        CustomDelivery delivery = new CustomDelivery() {};
        configuration.setSessionDelivery(delivery);

        sessionTracker.startSession(new Date(), true);
        sessionTracker.shutdown();
        assertTrue(delivery.recentRequest instanceof SessionPayload);
        assertEquals(1, delivery.count.get());
    }

    @Test
    public void testMultiShutdown() {
        CustomDelivery delivery = new CustomDelivery() {};
        configuration.setSessionDelivery(delivery);

        sessionTracker.startSession(new Date(), true);
        sessionTracker.shutdown();
        sessionTracker.shutdown(); // second should have no effect
        assertTrue(delivery.recentRequest instanceof SessionPayload);
        assertEquals(1, delivery.count.get());
    }

    abstract static class CustomDelivery implements Delivery {
        private boolean delivered;
        private AtomicInteger count = new AtomicInteger(0);
        private Object recentRequest;

        @Override
        public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
            this.recentRequest = object;
            delivered = true;
            count.getAndIncrement();
        }

        @Override
        public void close() {
        }
    }
}
