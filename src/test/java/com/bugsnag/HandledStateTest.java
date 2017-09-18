package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class HandledStateTest {

    @Test
    public void testHandledEventState() throws Throwable {
        HandledState state = new HandledState(Severity.WARNING, null, null);
        assertNotNull(state);
        assertFalse(state.isUnhandled());
        assertTrue(state.isDefaultSeverity(Severity.WARNING));
        assertFalse(state.isDefaultSeverity(Severity.INFO));
    }

    @Test
    public void testUnhandledEventState() throws Throwable {
        HandledState state = new HandledState(Severity.ERROR,
                HandledState.SeverityReasonType.EXCEPTION_HANDLER, null);
        assertNotNull(state);
        assertTrue(state.isUnhandled());
        assertTrue(state.isDefaultSeverity(Severity.ERROR));
        assertFalse(state.isDefaultSeverity(Severity.WARNING));
    }

    @Test
    public void testSeverityPayload() throws Throwable {
        HandledState eh = new HandledState(Severity.ERROR,
                HandledState.SeverityReasonType.LOG_LEVEL, "warn");
        assertNotNull(eh);
        assertTrue(eh.getSeverityReasonType() == HandledState.SeverityReasonType.LOG_LEVEL);
        assertEquals(eh.getDescription(), "warn");
    }

}