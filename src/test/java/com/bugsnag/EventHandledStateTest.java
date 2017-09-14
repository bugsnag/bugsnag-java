package com.bugsnag;

import org.junit.Test;

import static com.bugsnag.EventHandledState.*;
import static org.junit.Assert.*;

public class EventHandledStateTest {

    @Test
    public void testHandledEventState() throws Throwable {
        EventHandledState state = new EventHandledState(Severity.WARNING, null, null);
        assertNotNull(state);
        assertFalse(state.isUnhandled());
        assertTrue(state.isDefaultSeverity(Severity.WARNING));
        assertFalse(state.isDefaultSeverity(Severity.INFO));
    }

    @Test
    public void testUnhandledEventState() throws Throwable {
        EventHandledState state = new EventHandledState(Severity.ERROR, SeverityReasonType.EXCEPTION_HANDLER, null);
        assertNotNull(state);
        assertTrue(state.isUnhandled());
        assertTrue(state.isDefaultSeverity(Severity.ERROR));
        assertFalse(state.isDefaultSeverity(Severity.WARNING));
    }

    @Test
    public void testSeverityPayload() throws Throwable {
        EventHandledState eh = new EventHandledState(Severity.ERROR, SeverityReasonType.LOG_LEVEL, "warn");
        assertNotNull(eh);
        assertTrue(eh.getSeverityReasonType() == SeverityReasonType.LOG_LEVEL);
        assertEquals(eh.getDescription(), "warn");
    }

}