package com.bugsnag;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;


public class HandledStateTest {

    @Test
    public void testHandled() throws java.lang.Exception {
        HandledState handled = HandledState.newInstance(
                HandledState.SeverityReasonType.REASON_HANDLED_EXCEPTION);
        assertNotNull(handled);
        assertFalse(handled.isUnhandled());
        assertEquals(Severity.WARNING, handled.getCurrentSeverity());
    }

    @Test
    public void testUnhandled() throws java.lang.Exception {
        HandledState unhandled = HandledState.newInstance(
                HandledState.SeverityReasonType.REASON_UNHANDLED_EXCEPTION);
        assertNotNull(unhandled);
        assertTrue(unhandled.isUnhandled());
        assertEquals(Severity.ERROR, unhandled.getCurrentSeverity());
    }

    @Test
    public void testUserSpecified() throws java.lang.Exception {
        HandledState userSpecified = HandledState.newInstance(
                HandledState.SeverityReasonType.REASON_USER_SPECIFIED, Severity.INFO);
        assertNotNull(userSpecified);
        assertFalse(userSpecified.isUnhandled());
        assertEquals(Severity.INFO, userSpecified.getCurrentSeverity());
    }

    @Test
    public void testCallbackSpecified() throws java.lang.Exception {
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
    public void testInvalidUserSpecified() throws java.lang.Exception {
        HandledState.newInstance(
                HandledState.SeverityReasonType.REASON_CALLBACK_SPECIFIED);
    }

}