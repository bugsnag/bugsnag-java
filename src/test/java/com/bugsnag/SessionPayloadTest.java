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
        sessionCounts.add(new SessionCount(new Date(), 50));
        SessionPayload payload = new SessionPayload(sessionCounts, new Configuration("api-key"));
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

        JsonNode device = rootNode.get("device");
        assertNotNull(device);
        assertEquals(3, device.size());
    }

}