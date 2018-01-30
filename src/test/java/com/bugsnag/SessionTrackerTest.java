package com.bugsnag;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class SessionTrackerTest {

    private SessionTracker sessionTracker;
    private Configuration configuration;

    @Before
    public void setUp() throws Throwable {
        configuration = new Configuration("api-key");
        sessionTracker = new SessionTracker(configuration);
        assertNull(sessionTracker.getSession());
    }

    @Test
    public void startManualSession() throws Throwable {
        sessionTracker.startNewSession(new Date(), false);
        assertNotNull(sessionTracker.getSession());
    }

    @Test
    public void startAutoSessionDisabled() throws Throwable {
        sessionTracker.startNewSession(new Date(), true);
        assertNull(sessionTracker.getSession());
    }

    @Test
    public void startAutoSessionEnabled() throws Throwable {
        configuration.setAutoCaptureSessions(true);
        sessionTracker.startNewSession(new Date(), true);
        assertNotNull(sessionTracker.getSession());
    }

    @Test
    public void startTwoSessionsSameThread() throws Throwable {
        sessionTracker.startNewSession(new Date(), false);
        Session first = sessionTracker.getSession();

        sessionTracker.startNewSession(new Date(), false);
        Session second = sessionTracker.getSession();
        assertNotEquals(first, second);
    }

    @Test
    public void startTwoSessionsDiffThread() throws Throwable {
        sessionTracker.startNewSession(new Date(), false);
        final Session first = sessionTracker.getSession();
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                sessionTracker.startNewSession(new Date(), false);
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
        sessionTracker.startNewSession(new Date(), false);
        assertNull(sessionTracker.getSession());
    }

    @Test
    public void enabledReleaseStage() throws Throwable {
        configuration.notifyReleaseStages = new String[]{"prod"};
        configuration.releaseStage = "prod";
        sessionTracker.startNewSession(new Date(), false);
        assertNotNull(sessionTracker.getSession());
    }

    @Test(timeout = 100)
    public void addManySessions() throws Throwable {
        for (int k = 0; k < 1000; k++) {
            sessionTracker.startNewSession(new Date(), false);
        }
    }

}