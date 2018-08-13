package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Collections;
import java.util.Map;

public class HandledStateTest {

    @Test
    public void testHandled() {
        HandledState handled = HandledState.newInstance(
                HandledState.SeverityReasonType.REASON_HANDLED_EXCEPTION);
        assertNotNull(handled);
        assertFalse(handled.isUnhandled());
        assertEquals(Severity.WARNING, handled.getCurrentSeverity());
    }

    @Test
    public void testUnhandled() {
        HandledState unhandled = HandledState.newInstance(
                HandledState.SeverityReasonType.REASON_UNHANDLED_EXCEPTION);
        assertNotNull(unhandled);
        assertTrue(unhandled.isUnhandled());
        assertEquals(Severity.ERROR, unhandled.getCurrentSeverity());
    }

    @Test
    public void testUserSpecified() {
        HandledState userSpecified = HandledState.newInstance(
                HandledState.SeverityReasonType.REASON_USER_SPECIFIED, Severity.INFO);
        assertNotNull(userSpecified);
        assertFalse(userSpecified.isUnhandled());
        assertEquals(Severity.INFO, userSpecified.getCurrentSeverity());
    }

    @Test
    public void testCallbackSpecified() {
        HandledState handled = HandledState.newInstance(
                HandledState.SeverityReasonType.REASON_HANDLED_EXCEPTION);

        assertEquals(
                HandledState.SeverityReasonType.REASON_HANDLED_EXCEPTION,
                handled.calculateSeverityReasonType());

        handled.setCurrentSeverity(Severity.INFO);
        assertEquals(
                HandledState.SeverityReasonType.REASON_CALLBACK_SPECIFIED,
                handled.calculateSeverityReasonType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidUserSpecified() {
        HandledState.newInstance(
                HandledState.SeverityReasonType.REASON_CALLBACK_SPECIFIED);
    }

    @Test
    public void testUnhandledMiddleware() {
        Map<String, String> attributes = Collections.singletonMap("framework", "Spring");
        HandledState unhandled = HandledState.newInstance(
                HandledState.SeverityReasonType.REASON_UNHANDLED_EXCEPTION_MIDDLEWARE, attributes);
        assertNotNull(unhandled);
        assertTrue(unhandled.isUnhandled());
        assertEquals(Severity.ERROR, unhandled.getCurrentSeverity());
        assertEquals(HandledState.SeverityReasonType.REASON_UNHANDLED_EXCEPTION_MIDDLEWARE,
                unhandled.getSeverityReasonType());
        assertEquals("Spring", unhandled.getSeverityReasonAttributes().get("framework"));
    }

    @Test
    public void testUnhandledExceptionClass() {
        Map<String, String> attributes =
                Collections.singletonMap("exceptionClass", "TypeMismatchException");
        HandledState unhandled = HandledState.newInstance(
                HandledState.SeverityReasonType.REASON_EXCEPTION_CLASS, attributes,
                Severity.INFO, true);
        assertNotNull(unhandled);
        assertTrue(unhandled.isUnhandled());
        assertEquals(Severity.INFO, unhandled.getCurrentSeverity());
        assertEquals(HandledState.SeverityReasonType.REASON_EXCEPTION_CLASS,
                unhandled.getSeverityReasonType());
        assertEquals("TypeMismatchException",
                unhandled.getSeverityReasonAttributes().get("exceptionClass"));
    }

    @Test
    public void testHandledExceptionClass() {
        Map<String, String> attributes =
                Collections.singletonMap("exceptionClass", "TypeMismatchException");
        HandledState handled = HandledState.newInstance(
                HandledState.SeverityReasonType.REASON_EXCEPTION_CLASS, attributes,
                Severity.INFO, false);
        assertNotNull(handled);
        assertFalse(handled.isUnhandled());
        assertEquals(Severity.INFO, handled.getCurrentSeverity());
        assertEquals(HandledState.SeverityReasonType.REASON_EXCEPTION_CLASS,
                handled.getSeverityReasonType());
        assertEquals("TypeMismatchException",
                handled.getSeverityReasonAttributes().get("exceptionClass"));
    }
}
