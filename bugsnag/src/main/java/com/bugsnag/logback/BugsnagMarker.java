package com.bugsnag.logback;

import com.bugsnag.callbacks.Callback;

import org.slf4j.Marker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Marker used to pass a callback through a logger call
 */
public class BugsnagMarker implements Marker {

    private static final long serialVersionUID = -8536034181100363313L;

    private static final String BUGSNAG_MARKER_NAME = "BUGSNAG_MARKER";

    private Callback callback;

    private List<Marker> references = new ArrayList<Marker>();

    public BugsnagMarker(Callback callback) {
        this.callback = callback;
    }

    public Callback getCallback() {
        return callback;
    }

    @Override
    public String getName() {
        return BUGSNAG_MARKER_NAME;
    }

    @Override
    public void add(Marker reference) {
        references.add(reference);
    }

    @Override
    public boolean remove(Marker reference) {
        return references.remove(reference);
    }

    @Override
    public boolean hasChildren() {
        return hasReferences();
    }

    @Override
    public boolean hasReferences() {
        return references.size() > 0;
    }

    @Override
    public Iterator<Marker> iterator() {
        return references.iterator();
    }

    @Override
    public boolean contains(Marker other) {
        return references.contains(other);
    }

    @Override
    public boolean contains(String name) {
        for (Marker reference : references) {
            if (reference.getName().equals(name)) {
                return true;
            }
        }

        return false;
    }
}
