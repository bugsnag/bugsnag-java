package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.bugsnag.delivery.OutputStreamDelivery;

import com.bugsnag.serialization.Serializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.Exception;
import java.util.Collections;

public class HandledStatePayloadTest {

    private Configuration config;

    @Before
    public void setUp() throws Exception {
        config = new Configuration("123");
    }

    @Test
    public void testBasicSerialisation() throws Throwable {
        Report report = reportFromHandledState(HandledState.newInstance(
                HandledState.SeverityReasonType.REASON_UNHANDLED_EXCEPTION));
        JsonNode payload = getJsonPayloadFromReport(report);

        JsonNode event = getEvent(payload);
        assertEquals("error", event.get("severity").asText());
    }

    @Test
    public void testHandledSerialisation() throws java.lang.Exception {
        Report report = reportFromHandledState(HandledState.newInstance(
                HandledState.SeverityReasonType.REASON_HANDLED_EXCEPTION));
        JsonNode payload = getJsonPayloadFromReport(report);

        assertEquals("warning", payload.get("severity").asText());
        assertEquals(false, payload.get("unhandled").booleanValue());

        JsonNode severityReason = payload.get("severityReason");
        assertNotNull(severityReason);
        assertEquals(HandledState.SeverityReasonType.REASON_HANDLED_EXCEPTION
                        .toString(),
                severityReason.get("type").asText());
        assertNull(severityReason.get("attributes"));
    }

    @Test
    public void testUnhandledSerialisation() throws java.lang.Exception {
        Report report = reportFromHandledState(HandledState.newInstance(
                HandledState.SeverityReasonType.REASON_UNHANDLED_EXCEPTION));
        JsonNode payload = getJsonPayloadFromReport(report);

        assertEquals("error", payload.get("severity").asText());
        assertEquals(true, payload.get("unhandled").booleanValue());

        JsonNode severityReason = payload.get("severityReason");
        assertNotNull(severityReason);
        assertEquals(HandledState.SeverityReasonType.REASON_UNHANDLED_EXCEPTION
                        .toString(),
                severityReason.get("type").asText());
        assertNull(severityReason.get("attributes"));
    }

    @Test
    public void testUserSpecifiedSerialisation() throws java.lang.Exception {
        Report report = reportFromHandledState(HandledState.newInstance(
                HandledState.SeverityReasonType.REASON_USER_SPECIFIED, Severity.WARNING));
        JsonNode payload = getJsonPayloadFromReport(report);

        assertEquals("warning", payload.get("severity").asText());
        assertEquals(false, payload.get("unhandled").booleanValue());

        JsonNode severityReason = payload.get("severityReason");
        assertNotNull(severityReason);
        assertEquals(HandledState.SeverityReasonType.REASON_USER_SPECIFIED
                        .toString(),
                severityReason.get("type").asText());
        assertNull(severityReason.get("attributes"));
    }

    @Test
    public void testCallbackSpecified() throws Exception {
        Report report = reportFromHandledState(HandledState.newInstance(
                HandledState.SeverityReasonType.REASON_USER_SPECIFIED));
        report.setSeverity(Severity.INFO);
        JsonNode payload = getJsonPayloadFromReport(report);

        assertEquals("info", payload.get("severity").asText());
        assertEquals(false, payload.get("unhandled").booleanValue());

        JsonNode severityReason = payload.get("severityReason");
        assertNotNull(severityReason);
        assertEquals(HandledState.SeverityReasonType.REASON_CALLBACK_SPECIFIED
                        .toString(),
                severityReason.get("type").asText());
        assertNull(severityReason.get("attributes"));
    }

    private Report reportFromHandledState(HandledState handledState) {
        return new Report(config, new RuntimeException(), handledState);
    }

    private JsonNode getJsonPayloadFromReport(Report report) throws IOException {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        OutputStreamDelivery delivery = new OutputStreamDelivery(byteStream);
        delivery.deliver(new Serializer(), report, Collections.<String, String>emptyMap());

        String data = new String(byteStream.toByteArray());
        assertNotNull(data);
        return new ObjectMapper().readTree(data);
    }

    private JsonNode getEvent(JsonNode payload) {
        assertNotNull(payload);
        return payload;
    }

}
