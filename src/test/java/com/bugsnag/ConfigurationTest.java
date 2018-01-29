package com.bugsnag;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class ConfigurationTest {

    private Configuration config;

    @Before
    public void setUp() throws Throwable {
        config = new Configuration("foo");
    }

    @Test
    public void testDefaults() throws java.lang.Exception {
        assertFalse(config.shouldAutoCaptureSessions());
    }

    @Test
    public void testErrorApiHeaders() throws java.lang.Exception {
        Map<String, String> headers = config.getErrorApiHeaders();
        assertEquals(config.apiKey, headers.get("Bugsnag-Api-Key"));
        assertNotNull(headers.get("Bugsnag-Sent-At"));
        assertNotNull(headers.get("Bugsnag-Payload-Version"));
    }

    @Test
    public void testSessionApiHeaders() throws java.lang.Exception {
        Map<String, String> headers = config.getSessionApiHeaders();
        assertEquals(config.apiKey, headers.get("Bugsnag-Api-Key"));
        assertNotNull(headers.get("Bugsnag-Sent-At"));
        assertNotNull(headers.get("Bugsnag-Payload-Version"));
    }

}