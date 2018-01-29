package com.bugsnag;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.*;

public class NotificationTest {

    private Report report;
    private Configuration config;
    private final ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() throws Throwable {
        config = new Configuration("api-key");
        report = new Report(config, new RuntimeException());
    }

    private JsonNode generateJson(ObjectMapper mapper, Configuration config, Report report) throws IOException {
        Notification notification = new Notification(config, report);
        String json = mapper.writeValueAsString(notification);
        return mapper.readTree(json);
    }

    @Test
    public void testSessionSerialisation() throws Throwable {
        report.setSession(new Session("123", new Date()));
        JsonNode rootNode = generateJson(mapper, config, report);
        validateErrorReport(rootNode);

        // check contains a session
        JsonNode session = rootNode.get("events").get(0).get("session");
        assertNotNull(session);
        assertNotNull(session.get("id").asText());
        assertNotNull(session.get("startedAt").asText());

        JsonNode handledState = session.get("events");
        assertNotNull(handledState);
        assertNotNull(handledState.get("handled"));
        assertNotNull(handledState.get("unhandled"));
    }

    @Test
    public void testWithoutSessionSerialisation() throws Throwable {
        report.setSession(new Session("123", new Date()));
        JsonNode rootNode = generateJson(mapper, config, report);
        validateErrorReport(rootNode);
        assertNull(rootNode.get("events").get("session"));
    }

    private void validateErrorReport(JsonNode rootNode) throws Throwable {
        assertNotNull(rootNode);
        assertNotNull(rootNode.get("apiKey").asText());
        assertNotNull(rootNode.get("notifier"));

        JsonNode events = rootNode.get("events");
        assertNotNull(events);

        JsonNode event = events.get(0);
        assertNotNull(event);
    }

}