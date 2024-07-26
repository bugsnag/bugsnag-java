package com.bugsnag.serialization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;

public class Serializer implements ISerializer {
    private ObjectMapper mapper = new ObjectMapper();

    /**
     * Constructor.
     */
    // Use deprecated method to ensure we don't break with older versions of jackson
    @SuppressWarnings("deprecation")
    public Serializer() {
        mapper
            .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
            .setVisibilityChecker(
                mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
    }

    @Override
    public void writeToStream(OutputStream stream, Object object) throws SerializationException {
        try {
            mapper.writeValue(stream, object);
        } catch (IOException ex) {
            throw new SerializationException("Exception during serialization", ex);
        }
    }

    @Override
    public String toJson(Object object) throws SerializationException {
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException ex) {
            throw new SerializationException("Exception during serialization", ex);
        }
    }
}
