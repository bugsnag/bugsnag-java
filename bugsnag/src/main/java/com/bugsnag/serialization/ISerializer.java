package com.bugsnag.serialization;

import java.io.OutputStream;

public interface ISerializer {
    /**
     * Write the object to the stream.
     *
     * @param stream the stream to write the object to.
     * @param object the object to write to the stream.
     * @throws SerializationException the object could not be serialized.
     */
    void writeToStream(OutputStream stream, Object object) throws SerializationException;

    /**
     * Convert the object to a JSON string.
     *
     * @param object the object to convert to JSON.
     * @return the JSON string representation of the object.
     * @throws SerializationException the object could not be serialized.
     */
    String toJson(Object object) throws SerializationException;
}
