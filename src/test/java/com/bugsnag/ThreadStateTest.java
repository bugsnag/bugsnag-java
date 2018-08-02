package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
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
        JsonNode root = serialiseThreadStateToJson();

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
        JsonNode root = serialiseThreadStateToJson();
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

    private JsonNode serialiseThreadStateToJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(threadStates);
        return mapper.readTree(json);
    }

}
