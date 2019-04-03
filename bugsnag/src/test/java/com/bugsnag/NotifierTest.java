package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

public class NotifierTest {

    private Report report;
    private Configuration config;
    private SessionPayload sessionPayload;

    /**
     * Initialises the objectmapper + report for conversion to json
     *
     * @throws Throwable the throwable
     */
    @Before
    public void setUp() throws Throwable {
        config = new Configuration("api-key");
        report = new Report(config, new RuntimeException());
        sessionPayload = new SessionPayload(Collections.<SessionCount>emptyList(), config);
    }

    @Test
    public void testNotificationSerialisation() throws Throwable {
        JsonNode payload = BugsnagTestUtils.mapReportToJson(config, this.report);
        JsonNode notifier = payload.get("notifier");

        assertEquals("Bugsnag Java", notifier.get("name").asText());
        assertEquals("https://github.com/bugsnag/bugsnag-java", notifier.get("url").asText());
        assertFalse(notifier.get("version").asText().isEmpty());
    }

    @Test
    public void testSessionSerialisation() throws Throwable {
        JsonNode payload = BugsnagTestUtils.mapSessionPayloadToJson(sessionPayload);
        JsonNode notifier = payload.get("notifier");

        assertEquals("Bugsnag Java", notifier.get("name").asText());
        assertEquals("https://github.com/bugsnag/bugsnag-java", notifier.get("url").asText());
        assertFalse(notifier.get("version").asText().isEmpty());
    }
}
