package com.bugsnag.util;

import com.bugsnag.serialization.SerializationException;
import com.bugsnag.serialization.Serializer;

import java.io.IOException;
import java.io.OutputStream;

public class CustomSerializer implements Serializer {
    @Override
    public void writeToStream(OutputStream stream, Object object) throws SerializationException {
        try {
            stream.write("foo".getBytes());
        } catch (IOException exception) {
            throw new SerializationException("Exception during serialization", exception);
        }
    }
}
