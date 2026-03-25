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

public class ThreadTest {

    private List<BugsnagThread> threads;
    private Configuration config;

    /**
     * Generates a list of current thread states
     *
     * @throws Exception if the thread state could not be constructed
     */
    @Before
    public void setUp() {
        config = new Configuration("apikey");
        Map<java.lang.Thread, StackTraceElement[]> stackTraces = java.lang.Thread.getAllStackTraces();
        java.lang.Thread currentThread = java.lang.Thread.currentThread();
        threads = BugsnagThread.getLiveThreads(config, currentThread, stackTraces, null);
    }

    @Test
    public void testThreadStateContainsCurrentThread() {
        int count = 0;

        for (BugsnagThread thread : threads) {
            if (thread.getId() == java.lang.Thread.currentThread().getId()) {
                count++;
            }
        }
        assertEquals(1, count);
    }

    @Test
    public void testThreadName() {
        for (BugsnagThread thread : threads) {
            assertNotNull(thread.getName());
        }
    }

    @Test
    public void testThreadStacktrace() {
        for (BugsnagThread thread : threads) {
            List<Stackframe> stacktrace = thread.getStacktrace();
            assertNotNull(stacktrace);
        }
    }

    /**
     * Verifies that the required values for 'thread' are serialised as an array
     */
    @Test
    public void testSerialisation() throws IOException {
        JsonNode root = serialiseThreadStateToJson(threads);

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
        JsonNode root = serialiseThreadStateToJson(threads);
        long currentThreadId = java.lang.Thread.currentThread().getId();
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
        Map<java.lang.Thread, StackTraceElement[]> threads = java.lang.Thread.getAllStackTraces();
        threads.remove(java.lang.Thread.currentThread());
        java.lang.Thread otherThread = threads.keySet().iterator().next();
        Map<java.lang.Thread, StackTraceElement[]> allStackTraces = java.lang.Thread.getAllStackTraces();
        List<BugsnagThread> state
                = BugsnagThread.getLiveThreads(config, otherThread, allStackTraces, null);

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
     * {@link java.lang.Thread#getAllStackTraces()}, its stacktrace will still be serialised
     */
    @Test
    public void testMissingCurrentThread() throws IOException {
        Map<java.lang.Thread, StackTraceElement[]> threads = java.lang.Thread.getAllStackTraces();
        java.lang.Thread currentThread = java.lang.Thread.currentThread();
        threads.remove(currentThread);

        List<BugsnagThread> state
                = BugsnagThread.getLiveThreads(config, currentThread, threads, null);

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
     * Verifies that a handled error uses {@link java.lang.Thread#getAllStackTraces()}
     * for the reporting thread stacktrace
     */
    @Test
    public void testHandledStacktrace() throws IOException {
        Map<java.lang.Thread, StackTraceElement[]> threads = java.lang.Thread.getAllStackTraces();
        java.lang.Thread currentThread = java.lang.Thread.currentThread();
        StackTraceElement[] expectedTrace = threads.get(currentThread);

        List<BugsnagThread> state
                = BugsnagThread.getLiveThreads(config, currentThread, threads, null);

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
     * Verifies that an unhandled error uses {@link BugsnagError#getStacktrace()}
     * for the reporting thread stacktrace
     */
    @Test
    public void testUnhandledStacktrace() throws IOException {
        Map<java.lang.Thread, StackTraceElement[]> threads = java.lang.Thread.getAllStackTraces();
        java.lang.Thread currentThread = java.lang.Thread.currentThread();
        RuntimeException exc = new RuntimeException("Whoops");
        StackTraceElement[] expectedTrace = exc.getStackTrace();

        List<BugsnagThread> state
                = BugsnagThread.getLiveThreads(config, currentThread, threads, exc);

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
    private JsonNode serialiseThreadStateToJson(List<BugsnagThread> threads) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper
                .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
                .setVisibilityChecker(mapper.getVisibilityChecker()
                        .with(JsonAutoDetect.Visibility.NONE));

        String json = mapper.writeValueAsString(threads);
        return mapper.readTree(json);
    }

}
