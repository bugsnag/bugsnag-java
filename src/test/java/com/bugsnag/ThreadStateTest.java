package com.bugsnag;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import java.lang.Exception;
import java.util.List;

public class ThreadStateTest {

    private List<ThreadState> threadStates;

    /**
     * Generates a list of current thread states
     *
     * @throws Exception if the thread state could not be constructed
     */
    @Before
    public void setUp() throws Exception {
        threadStates = ThreadState.getLiveThreads(new Configuration("apikey"));
    }

    @Test
    public void testThreadStateDoesNotContainCurrentThread() {

        for (ThreadState thread : threadStates) {
            if (thread.getId() == Thread.currentThread().getId()) {
                fail();
            }
        }

        // Just test that there is at least one thread
        assertTrue(threadStates.size() > 1);
    }

    @Test
    public void testThreadName() {
        for (ThreadState threadState : threadStates) {
            assertNotNull(threadState.getName());
        }
    }

    @Test
    public void testThreadStacktrace() {
        for (ThreadState threadState : threadStates) {
            List<Stackframe> stacktrace = threadState.getStacktrace();
            assertNotNull(stacktrace);
        }
    }
}
