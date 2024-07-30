package com.bugsnag.serialization;

import java.io.OutputStream;

/**
 * The Serializer is called to generate the JSON for an object to be added as metadata to a Bugsnag event.
 */
public interface Serializer {
    /**
     * Write the specified object to the provided stream to be used on metadata for a Bugsnag event.
     *
     * @param stream the stream to write the object to.
     * @param object the object to write to the stream.
     * @throws SerializationException the object could not be serialized.
     */
    void writeToStream(OutputStream stream, Object object) throws SerializationException;
}
