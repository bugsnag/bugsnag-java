package com.bugsnag;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import static org.junit.Assert.*;

public class SessionPayloadTest {

    private JsonNode rootNode;

    @Before
    public void setUp() throws Throwable {
        ObjectMapper mapper = new ObjectMapper();
        Collection<SessionCount> sessionCounts = new ArrayList<SessionCount>();
        SessionCount e = new SessionCount(new Date(1500000000000L));
        e.incrementSessionsStarted();
        e.incrementSessionsStarted();
        sessionCounts.add(e);
        Configuration configuration = new Configuration("api-key");
        configuration.appVersion = "1.2.3";
        configuration.releaseStage = "dev";
        SessionPayload payload = new SessionPayload(sessionCounts, configuration);
        String json = mapper.writeValueAsString(payload);
        rootNode = mapper.readTree(json);
    }

    @Test
    public void testJsonSerialisation() throws Throwable {
        assertNotNull(rootNode);

        JsonNode notifier = rootNode.get("notifier");
        assertNotNull(notifier);
        assertEquals(3, notifier.size());

        JsonNode app = rootNode.get("app");
        assertNotNull(app);
        assertEquals(2, app.size());

        JsonNode sessionCounts = rootNode.get("sessionCounts");
        assertNotNull(sessionCounts);
        assertEquals(1, sessionCounts.size());

        JsonNode sessionCount = sessionCounts.get(0);
        assertNotNull(sessionCount);
        assertEquals(2, sessionCount.size());

        assertEquals(sessionCount.get("sessionsStarted").intValue(), 2);
        assertEquals(sessionCount.get("startedAt").asText(), "2017-07-14T02:40:00Z");

        JsonNode device = rootNode.get("device");
        assertNotNull(device);
        assertEquals(3, device.size());
    }

}