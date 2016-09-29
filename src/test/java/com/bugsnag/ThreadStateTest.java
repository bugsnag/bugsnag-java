package com.bugsnag;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ThreadStateTest {

    @Test
    public void testThreadStateDoesNotContainCurrentThread() {
        List<ThreadState> threadStates = ThreadState.getLiveThreads(new Configuration("apikey"));

        for (ThreadState thread : threadStates) {
            if (thread.getId() == Thread.currentThread().getId()) {
                fail();
            }
        }

        // Just test that there is at least one thread
        assertTrue(threadStates.size() > 1);
    }
}
