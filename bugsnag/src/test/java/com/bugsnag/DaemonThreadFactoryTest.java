package com.bugsnag;

import static org.junit.Assert.assertTrue;

import com.bugsnag.util.DaemonThreadFactory;

import org.junit.Before;
import org.junit.Test;


/**
 * Tests for the Daemon thread factory internal logic
 */
public class DaemonThreadFactoryTest {

    private DaemonThreadFactory daemonThreadFactory;

    /**
     * Create the daemonThreadFactory before the tests
     */
    @Before
    public void createFactory() {
        daemonThreadFactory = new DaemonThreadFactory();
    }

    @Test
    public void testDaemonThreadFactory() {
        Thread testThread = daemonThreadFactory.newThread(null);

        // Check that the thread is as expected
        assertTrue(testThread.isDaemon());
    }
}
