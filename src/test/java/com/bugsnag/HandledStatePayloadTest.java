package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.bugsnag.callbacks.Callback;
import com.bugsnag.delivery.OutputStreamDelivery;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class HandledStatePayloadTest {

//    @Test
//    public void testPayloadGeneration() throws Throwable {
//        HandledState handledState = new HandledState(Severity.WARNING, null, null);
//        RuntimeException exception = new RuntimeException("Test");
//        JsonNode payload = getJsonPayloadFromThrowable(exception, handledState, false);
//        JsonNode event = getEvent(payload);
//        assertEquals("warning", event.get("severity").asText());
//    }
//
//    @Test
//    public void testUnhandledPayload() throws Throwable {
//        HandledState handledState = new HandledState(Severity.ERROR,
//                HandledState.SeverityReasonType.EXCEPTION_HANDLER, null);
//        RuntimeException exception = new RuntimeException();
//        JsonNode payload = getJsonPayloadFromThrowable(exception, handledState, false);
//        JsonNode event = getEvent(payload);
//
//        assertTrue(event.get("defaultSeverity").booleanValue());
//        assertTrue(event.get("unhandled").booleanValue());
//
//        JsonNode severityReason = event.get("severityReason");
//        assertNotNull(severityReason);
//        assertEquals("exception_handler", severityReason.get("type").asText());
//    }
//
//    @Test
//    public void testHandledPayload() throws Throwable {
//        HandledState handledState = new HandledState(Severity.WARNING, null, null);
//        JsonNode payload = getJsonPayloadFromThrowable(new RuntimeException(), handledState, false);
//        JsonNode event = getEvent(payload);
//
//        assertTrue(event.get("defaultSeverity").booleanValue());
//        assertFalse(event.get("unhandled").booleanValue());
//        assertNull(event.get("severityReason"));
//    }
//
//    @Test
//    public void testSeverityMutation() throws Throwable {
//        HandledState handledState = new HandledState(Severity.WARNING, null, null);
//        JsonNode payload = getJsonPayloadFromThrowable(new RuntimeException(), handledState, true);
//        JsonNode event = getEvent(payload);
//
//        assertFalse(event.get("defaultSeverity").booleanValue());
//        assertEquals("info", event.get("severity").asText());
//    }
//
//    private JsonNode getJsonPayloadFromThrowable(RuntimeException throwable,
//                                                 HandledState handledState,
//                                                 boolean changeSeverity) throws IOException {
//        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
//        Bugsnag bugsnag = new Bugsnag("apikey");
//        bugsnag.setDelivery(new OutputStreamDelivery(byteStream));
//
//        if (changeSeverity) {
//            bugsnag.addCallback(new Callback() {
//                @Override
//                public void beforeNotify(Report report) {
//                    report.setSeverity(Severity.INFO);
//                }
//            });
//        }
//        bugsnag.notify(throwable, handledState);
//
//        String data = new String(byteStream.toByteArray());
//        assertNotNull(data);
//        return new ObjectMapper().readTree(data);
//    }
//
//    private JsonNode getEvent(JsonNode payload) {
//        assertNotNull(payload);
//        assertEquals(3, payload.size());
//
//        JsonNode events = payload.get("events");
//        assertNotNull(events);
//        assertEquals(1, events.size());
//
//        JsonNode event = events.get(0);
//        assertNotNull(event);
//        return event;
//    }


    // TODO json serialisation tests!

}
