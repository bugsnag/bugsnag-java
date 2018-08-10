package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.lang.Exception;
import java.util.List;
import java.util.Map;

public class ThreadStateTest {

    private List<ThreadState> threadStates;
    private Configuration config;

    /**
     * Generates a list of current thread states
     *
     * @throws Exception if the thread state could not be constructed
     */
    @Before
    public void setUp() throws Exception {
        config = new Configuration("apikey");
        Map<Thread, StackTraceElement[]> stackTraces = Thread.getAllStackTraces();
        threadStates = ThreadState.getLiveThreads(config, Thread.currentThread(), stackTraces);
    }

    @Test
    public void testThreadStateContainsCurrentThread() {
        int count = 0;

        for (ThreadState thread : threadStates) {
            if (thread.getId() == Thread.currentThread().getId()) {
                count++;
            }
        }
        assertEquals(1, count);
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

    /**
     * Verifies that the required values for 'thread' are serialised as an array
     */
    @Test
    public void testSerialisation() throws Exception {
        JsonNode root = serialiseThreadStateToJson(threadStates);

        for (JsonNode jsonNode : root) {
            assertNotNull(jsonNode.get("id").asText());
            assertNotNull(jsonNode.get("name").asText());
            assertNotNull(jsonNode.get("stacktrace"));
        }
    }

    /**
     * Verifies that the current thread is serialised as an object, and that only this value
     * contains the errorReportingThread boolean flag
     */
    @Test
    public void testCurrentThread() throws Exception {
        JsonNode root = serialiseThreadStateToJson(threadStates);
        long currentThreadId = Thread.currentThread().getId();
        int currentThreadCount = 0;

        for (JsonNode jsonNode : root) {
            if (currentThreadId == jsonNode.get("id").asLong()) {
                assertTrue(jsonNode.get("errorReportingThread").asBoolean());
                currentThreadCount++;
            } else {
                assertFalse(jsonNode.has("errorReportingThread"));
            }
        }
        assertEquals(1, currentThreadCount);
    }

    /**
     * Verifies that a thread different from the current thread is serialised as an object,
     * and that only this value contains the errorReportingThread boolean flag
     */
    @Test
    public void testDifferentThread() throws Exception {
        Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
        threads.remove(Thread.currentThread());
        Thread otherThread = threads.keySet().iterator().next();
        Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        List<ThreadState> state = ThreadState.getLiveThreads(config, otherThread, allStackTraces);

        JsonNode root = serialiseThreadStateToJson(state);
        int currentThreadCount = 0;

        for (JsonNode jsonNode : root) {
            if (otherThread.getId() == jsonNode.get("id").asLong()) {
                assertTrue(jsonNode.get("errorReportingThread").asBoolean());
                currentThreadCount++;
            } else {
                assertFalse(jsonNode.has("errorReportingThread"));
            }
        }
        assertEquals(1, currentThreadCount);
    }

    /**
     * Verifies that if the current thread is missing from the available traces as reported by
     * {@link Thread#getAllStackTraces()}, its stacktrace will still be serialised
     */
    @Test
    public void testMissingCurrentThread() throws Exception {
        Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
        Thread currentThread = Thread.currentThread();
        threads.remove(currentThread);

        List<ThreadState> state
                = ThreadState.getLiveThreads(config, currentThread, threads);

        JsonNode root = serialiseThreadStateToJson(state);
        int currentThreadCount = 0;

        for (JsonNode jsonNode : root) {
            if (currentThread.getId() == jsonNode.get("id").asLong()) {
                assertTrue(jsonNode.get("errorReportingThread").asBoolean());
                assertTrue(jsonNode.get("stacktrace").size() > 0);
                currentThreadCount++;
            }
        }
        assertEquals(1, currentThreadCount);
    }

    private JsonNode serialiseThreadStateToJson(List<ThreadState> threadStates) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(threadStates);
        return mapper.readTree(json);
    }

}
