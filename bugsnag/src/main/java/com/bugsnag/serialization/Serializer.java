package com.bugsnag.serialization;

import java.io.OutputStream;

public interface Serializer {
    /**
     * Write the object to the stream.
     *
     * @param stream the stream to write the object to.
     * @param object the object to write to the stream.
     * @throws SerializationException the object could not be serialized.
     */
    void writeToStream(OutputStream stream, Object object) throws SerializationException;
}
