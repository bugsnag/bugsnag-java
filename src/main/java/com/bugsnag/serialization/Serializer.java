package com.bugsnag.serialization;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.OutputStream;

public class Serializer {
    private ObjectMapper mapper = new ObjectMapper();

    public Serializer() {
        mapper
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
            .setVisibility(
                mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE)
            );
    }

    public void writeToStream(OutputStream stream, Object object) throws SerializationException {
        try {
            mapper.writeValue(stream, object);
        } catch (IOException ex) {
            throw new SerializationException("Exception during serialization", ex);
        }
    }
}
