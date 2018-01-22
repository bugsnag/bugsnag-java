package com.bugsnag;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NotificationTest {

    private JsonNode rootNode;

    @Before
    public void setUp() throws Throwable {
        ObjectMapper mapper = new ObjectMapper();
        Configuration config = new Configuration("api-key");
        Report report = new Report(config, new RuntimeException());
        Notification notification = new Notification(config, report);
        String json = mapper.writeValueAsString(notification);
        rootNode = mapper.readTree(json);
    }

    @Test
    public void testJsonSerialisation() throws Throwable {
        assertNotNull(rootNode);
        assertNotNull(rootNode.get("apiKey").asText());
        assertNotNull(rootNode.get("notifier"));

        JsonNode events = rootNode.get("events");
        assertNotNull(events);

        JsonNode event = events.get(0);
        assertNotNull(event);

        // check contains a session
        JsonNode session = event.get("session");
        assertNotNull(session);

        assertNotNull(session.get("id").asText());
        assertNotNull(session.get("startedAt").asText());

        JsonNode handledState = session.get("events");
        assertNotNull(handledState);
        assertNotNull(handledState.get("handled"));
        assertNotNull(handledState.get("unhandled"));
    }

}