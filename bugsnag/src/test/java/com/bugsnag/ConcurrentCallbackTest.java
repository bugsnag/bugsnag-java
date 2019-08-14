package com.bugsnag;

import com.bugsnag.callbacks.Callback;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Ensures that if a callback is added or removed during iteration, a
 * {@link java.util.ConcurrentModificationException} is not thrown
 */
public class ConcurrentCallbackTest {

    private Bugsnag bugsnag;

    @Before
    public void initBugsnag() {
        bugsnag = new Bugsnag("apikey");
    }

    @After
    public void closeBugsnag() {
        bugsnag.close();
    }

    @Test
    public void testClientNotifyModification() {
        final Configuration config = bugsnag.getConfig();

        config.addCallback(new Callback() {
            @Override
            public void beforeNotify(Report report) {
                // modify the callback collection, when iterating to the next callback this should not crash
                config.addCallback(new Callback() {
                    @Override
                    public void beforeNotify(Report report) {
                    }
                });
            }
        });
        bugsnag.notify(new RuntimeException());
    }
}
