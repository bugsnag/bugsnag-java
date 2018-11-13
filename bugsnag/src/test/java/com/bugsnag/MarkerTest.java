package com.bugsnag;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.bugsnag.callbacks.Callback;
import com.bugsnag.logback.BugsnagMarker;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Marker;

import java.util.Iterator;


/**
 * Tests for the Bugsnag Marker internal logic
 */
public class MarkerTest {

    private BugsnagMarker marker;
    private Callback callback;

    /**
     * Create the marker before the tests
     */
    @Before
    public void createMarker() {
        callback = new Callback() {
            @Override
            public void beforeNotify(Report report) {

            }
        };

        marker = new BugsnagMarker(callback);
    }

    @Test
    public void testMarkerCallback() {

        // Check that the callback is set as expected
        assertEquals(callback, marker.getCallback());
    }

    @Test
    public void testMarkerName() {
        assertEquals("BUGSNAG_MARKER", marker.getName());
    }

    @Test
    public void testMarkerAddReference() {

        assertFalse(marker.hasReferences());
        assertFalse(marker.hasChildren());

        Marker newMarker = new BugsnagMarker(callback);
        marker.add(newMarker);

        assertTrue(marker.hasReferences());
        assertTrue(marker.hasChildren());
        assertTrue(marker.contains(newMarker));
        assertTrue(marker.contains(newMarker.getName()));

        Iterator<Marker> iterator = marker.iterator();
        assertEquals(newMarker,iterator.next());
        assertFalse(iterator.hasNext());
    }

    @Test
    public void testMarkerRemoveReference() {

        Marker newMarker = new BugsnagMarker(callback);
        marker.add(newMarker);

        assertTrue(marker.hasReferences());
        assertTrue(marker.hasChildren());

        marker.remove(newMarker);

        assertFalse(marker.hasReferences());
        assertFalse(marker.hasChildren());
    }
}
