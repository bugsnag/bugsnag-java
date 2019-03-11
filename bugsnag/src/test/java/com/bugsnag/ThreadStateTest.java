package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
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
    public void setUp() {
        config = new Configuration("apikey");
        Map<Thread, StackTraceElement[]> stackTraces = Thread.getAllStackTraces();
        Thread currentThread = Thread.currentThread();
        threadStates = ThreadState.getLiveThreads(config, currentThread, stackTraces, null);
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
    public void testSerialisation() throws IOException {
        JsonNode root = serialiseThreadStateToJson(threadStates);

        for (JsonNode jsonNode : root) {
            assertNotNull(jsonNode.get("id").asText());
            assertNotNull(jsonNode.get("name").asText());
        }
    }

    /**
     * Verifies that the current thread is serialised as an object, and that only this value
     * contains the errorReportingThread boolean flag
     */
    @Test
    public void testCurrentThread() throws IOException {
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
    public void testDifferentThread() throws IOException {
        Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
        threads.remove(Thread.currentThread());
        Thread otherThread = threads.keySet().iterator().next();
        Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();
        List<ThreadState> state
                = ThreadState.getLiveThreads(config, otherThread, allStackTraces, null);

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
    public void testMissingCurrentThread() throws IOException {
        Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
        Thread currentThread = Thread.currentThread();
        threads.remove(currentThread);

        List<ThreadState> state
                = ThreadState.getLiveThreads(config, currentThread, threads, null);

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


    /**
     * Verifies that a handled error uses {@link Thread#getAllStackTraces()}
     * for the reporting thread stacktrace
     */
    @Test
    public void testHandledStacktrace() throws IOException {
        Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
        Thread currentThread = Thread.currentThread();
        StackTraceElement[] expectedTrace = threads.get(currentThread);

        List<ThreadState> state
                = ThreadState.getLiveThreads(config, currentThread, threads, null);

        JsonNode root = serialiseThreadStateToJson(state);
        int currentThreadCount = 0;

        for (JsonNode jsonNode : root) {
            if (currentThread.getId() == jsonNode.get("id").asLong()) {
                currentThreadCount++;

                // the thread id + name should always be used
                assertEquals(currentThread.getName(), jsonNode.get("name").asText());

                // stacktrace should come from the thread (check same length and line numbers)
                JsonNode stacktrace = jsonNode.get("stacktrace");
                assertEquals(expectedTrace.length, stacktrace.size());

                for (int k = 0; k < expectedTrace.length; k++) {
                    JsonNode obj = stacktrace.get(k);
                    JsonNode lineNumber = obj.get("lineNumber");
                    assertEquals(expectedTrace[k].getLineNumber(), lineNumber.intValue());
                }
            }
        }
        assertEquals(1, currentThreadCount);
    }

    /**
     * Verifies that an unhandled error uses {@link com.bugsnag.Exception#getStacktrace()}
     * for the reporting thread stacktrace
     */
    @Test
    public void testUnhandledStacktrace() throws IOException {
        Map<Thread, StackTraceElement[]> threads = Thread.getAllStackTraces();
        Thread currentThread = Thread.currentThread();
        RuntimeException exc = new RuntimeException("Whoops");
        StackTraceElement[] expectedTrace = exc.getStackTrace();

        List<ThreadState> state
                = ThreadState.getLiveThreads(config, currentThread, threads, exc);

        JsonNode root = serialiseThreadStateToJson(state);
        int currentThreadCount = 0;

        for (JsonNode jsonNode : root) {
            if (currentThread.getId() == jsonNode.get("id").asLong()) {
                currentThreadCount++;

                // the thread id + name should always be used
                assertEquals(currentThread.getName(), jsonNode.get("name").asText());

                // stacktrace should come from the thread (check same length and line numbers)
                JsonNode stacktrace = jsonNode.get("stacktrace");
                assertEquals(expectedTrace.length, stacktrace.size());

                for (int k = 0; k < expectedTrace.length; k++) {
                    JsonNode obj = stacktrace.get(k);
                    JsonNode lineNumber = obj.get("lineNumber");
                    assertEquals(expectedTrace[k].getLineNumber(), lineNumber.intValue());
                }
            }
        }
        assertEquals(1, currentThreadCount);
    }

    @SuppressWarnings("deprecation")
    private JsonNode serialiseThreadStateToJson(List<ThreadState> threadStates) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
                .setVisibilityChecker(mapper.getVisibilityChecker()
                        .with(JsonAutoDetect.Visibility.NONE));

        String json = mapper.writeValueAsString(threadStates);
        return mapper.readTree(json);
    }

}
