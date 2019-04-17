package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;


public class SessionPayloadTest {

    private JsonNode rootNode;

    /**
     * Initialises the session payload for serialisation
     * @throws Throwable the throwable
     */
    @Before
    public void setUp() throws Throwable {
        Collection<SessionCount> sessionCounts = new ArrayList<SessionCount>();
        SessionCount sessionCount = new SessionCount(new Date(1500000000000L));
        sessionCount.incrementSessionsStarted();
        sessionCount.incrementSessionsStarted();
        sessionCounts.add(sessionCount);
        Configuration configuration = new Configuration("api-key");
        configuration.appVersion = "1.2.3";
        configuration.releaseStage = "dev";
        SessionPayload payload = new SessionPayload(sessionCounts, configuration);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(payload);
        rootNode = mapper.readTree(json);
    }

    @Test
    public void testJsonSerialisation() {
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
        assertEquals(4, device.size());
    }

}
