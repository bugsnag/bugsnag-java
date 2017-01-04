package com.bugsnag.serialization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;

public class Serializer {
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Constructor.
     */
    @SuppressWarnings("deprecation") // Use deprecated method to ensure we don't break with older versions of jackson
    public Serializer() {
        mapper
            .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
            .setVisibilityChecker(
                mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
    }

    /**
     * Write the object to the stream.
     *
     * @param stream the stream to write the object to.
     * @param object the object to write to the stream.
     * @throws SerializationException the object could not be serialized.
     */
    public void writeToStream(OutputStream stream, Object object) throws SerializationException {
        try {
            mapper.writeValue(stream, object);
        } catch (IOException ex) {
            throw new SerializationException("Exception during serialization", ex);
        }
    }
}
