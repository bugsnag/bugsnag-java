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

    /**
     * Initialises config + session tracker
     *
     * @throws Throwable the throwable
     */
    @Before
    public void setUp() throws Throwable {
        configuration = new Configuration("api-key");
        sessionTracker = new SessionTracker(configuration);
        assertNull(sessionTracker.getSession());
    }

    @Test
    public void startManualSession() throws Throwable {
        sessionTracker.startSession(new Date(), false);
        assertNotNull(sessionTracker.getSession());
    }

    @Test
    public void startAutoSessionDisabled() throws Throwable {
        configuration.setAutoCaptureSessions(false);
        sessionTracker.startSession(new Date(), true);
        assertNull(sessionTracker.getSession());
    }

    @Test
    public void startAutoSessionEnabled() throws Throwable {
        sessionTracker.startSession(new Date(), true);
        assertNotNull(sessionTracker.getSession());
    }

    @Test
    public void startTwoSessionsSameThread() throws Throwable {
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
    public void disabledReleaseStage() throws Throwable {
        configuration.notifyReleaseStages = new String[]{"prod"};
        configuration.releaseStage = "dev";
        sessionTracker.startSession(new Date(), false);
        assertNull(sessionTracker.getSession());
    }

    @Test
    public void enabledReleaseStage() throws Throwable {
        configuration.notifyReleaseStages = new String[]{"prod"};
        configuration.releaseStage = "prod";
        sessionTracker.startSession(new Date(), false);
        assertNotNull(sessionTracker.getSession());
    }

    @Test(timeout = 200)
    public void addManySessions() throws Throwable {
        for (int k = 0; k < 1000; k++) {
            sessionTracker.startSession(new Date(), false);
        }
    }

    @Test
    public void zeroSessionDelivery() throws Throwable {
        CustomDelivery sessionDelivery = new CustomDelivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                super.deliver(serializer, object, headers);
                fail("Should not be called if no sessions enqueued");
            }
        };
        configuration.sessionDelivery = sessionDelivery;
        sessionTracker.flushSessions(new Date());
        assertFalse(sessionDelivery.delivered);
    }

    @Test
    public void noDateChangeSessionDelivery() throws Throwable {
        CustomDelivery sessionDelivery = new CustomDelivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
                super.deliver(serializer, object, headers);
                fail("Should not be called if date has not exceeded batch period");
            }
        };
        configuration.sessionDelivery = sessionDelivery;
        sessionTracker.startSession(new Date(1309209859), false);
        sessionTracker.flushSessions(new Date(1309209859));
        assertFalse(sessionDelivery.delivered);
    }

    @Test
    public void multiSessionDelivery() throws Throwable {
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
        configuration.sessionDelivery = sessionDelivery;
        sessionTracker.startSession(new Date(5092340L), false);
        sessionTracker.startSession(new Date(125098234L), false);
        sessionTracker.startSession(new Date(1509207501L), false);
        sessionTracker.startSession(new Date(1509209834L), false);
        sessionTracker.flushSessions(new Date(1609209834L));
        assertTrue(sessionDelivery.delivered);
    }

    @Test
    public void sessionDeliveryDiffMin() throws Throwable {
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
        configuration.sessionDelivery = sessionDelivery;

        // 2 mins apart
        sessionTracker.startSession(new Date(10000000L), false);
        sessionTracker.flushSessions(new Date(10120000L));
        assertTrue(sessionDelivery.delivered);
    }

    @Test
    public void sessionDeliverySameMin() throws Throwable {
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
        configuration.sessionDelivery = sessionDelivery;

        // 1 hour apart
        sessionTracker.startSession(new Date(10000000L), false);
        sessionTracker.flushSessions(new Date(13600000L));
        assertTrue(sessionDelivery.delivered);
    }

    @Test
    public void sessionDeliveryMultiFlush() throws Throwable {
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
        configuration.sessionDelivery = sessionDelivery;

        // 1 hour apart
        sessionTracker.startSession(new Date(10000000L), false);
        sessionTracker.flushSessions(new Date(13600000L));
        sessionTracker.flushSessions(new Date(13600000L));
        assertTrue(sessionDelivery.delivered);
        assertEquals(1, sessionDelivery.count.get());
    }

    @Test
    public void sessionDeliveryShutdown() throws Throwable {
        CustomDelivery sessionDelivery = new CustomDelivery() {};
        configuration.sessionDelivery = sessionDelivery;
        sessionTracker.startSession(new Date(10000000L), false);
        sessionTracker.setShuttingDown(true);
        sessionTracker.flushSessions(new Date(10120000L));
        assertFalse(sessionDelivery.delivered);
    }

    @Test
    public void zeroSessionCount() throws Throwable {
        CustomDelivery sessionDelivery = new CustomDelivery() {};
        configuration.sessionDelivery = sessionDelivery;
        sessionTracker.flushSessions(new Date(10120000L));
        sessionTracker.flushSessions(new Date(14000000L));
        assertFalse(sessionDelivery.delivered);
    }

    abstract static class CustomDelivery implements Delivery {
        boolean delivered;
        AtomicInteger count = new AtomicInteger(0);

        @Override
        public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
            delivered = true;
            count.getAndIncrement();
        }

        @Override
        public void close() {
        }
    }
}
