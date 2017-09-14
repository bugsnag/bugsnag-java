package com.bugsnag;

import org.junit.Test;

import static com.bugsnag.EventHandledState.*;
import static org.junit.Assert.*;

public class EventHandledStateTest {

    @Test
    public void testHandledEventState() throws Throwable {
        EventHandledState state = new EventHandledState(Severity.WARNING, null);
        assertNotNull(state);
        assertFalse(state.isUnhandled());
        assertTrue(state.isDefaultSeverity(Severity.WARNING));
        assertFalse(state.isDefaultSeverity(Severity.INFO));
    }

    @Test
    public void testUnhandledEventState() throws Throwable {
        EventHandledState state = new EventHandledState(Severity.ERROR, SeverityReasonType.EXCEPTION_HANDLER);
        assertNotNull(state);
        assertTrue(state.isUnhandled());
        assertTrue(state.isDefaultSeverity(Severity.ERROR));
        assertFalse(state.isDefaultSeverity(Severity.WARNING));
    }

}