package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;


public class NotificationTest {

    private Report report;
    private Configuration config;
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Initialises the objectmapper + report for conversion to json
     *
     * @throws Throwable the throwable
     */
    @Before
    public void setUp() throws Throwable {
        config = new Configuration("api-key");
        config.appVersion = "1.2.3";
        config.releaseStage = "dev";
        report = new Report(config, new RuntimeException());
    }

    private JsonNode generateJson(ObjectMapper mapper,
                                  Configuration config,
                                  Report report) throws IOException {
        Notification notification = new Notification(config, report);
        String json = mapper.writeValueAsString(notification);
        return mapper.readTree(json);
    }

    @Test
    public void testSessionSerialisation() throws Throwable {
        report.setSession(new Session("123", new Date(1500000000000L)));
        JsonNode rootNode = generateJson(mapper, config, report);
        validateErrorReport(rootNode);

        // check contains a session
        JsonNode session = rootNode.get("events").get(0).get("session");
        assertNotNull(session);
        assertNotNull(session.get("id").asText());
        assertEquals("2017-07-14T02:40:00Z", session.get("startedAt").asText());

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

        assertEquals("warning", event.get("severity").asText());
        assertEquals("4", event.get("payloadVersion").asText());
        assertFalse(event.get("unhandled").asBoolean());

        JsonNode device = event.get("device");
        assertNotNull(device);
        assertNotNull(device.get("hostname").asText());
        assertNotNull(device.get("osName").asText());
        assertNotNull(device.get("osVersion").asText());

        JsonNode app = event.get("app");
        assertNotNull(app);
        assertNotNull(app.get("releaseStage").asText());
        assertNotNull(app.get("version").asText());
    }

}
