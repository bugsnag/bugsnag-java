package com.bugsnag;

import static org.junit.Assert.assertEquals;

import com.bugsnag.serialization.SerializationException;
import com.bugsnag.serialization.Serializer;
import com.bugsnag.util.CustomSerializer;

import org.junit.Test;

import java.io.ByteArrayOutputStream;

public class SerializerTest {

    @Test
    public void testCustomSerializer() throws SerializationException {
        Serializer customSerializer = new CustomSerializer();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        customSerializer.writeToStream(out, new Object());

        assertEquals("foo", out.toString());
    }
}
