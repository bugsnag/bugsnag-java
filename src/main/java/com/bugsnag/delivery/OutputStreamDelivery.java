package com.bugsnag.delivery;

import com.bugsnag.serialization.Serializer;
import com.bugsnag.serialization.SerializationException;

import java.io.IOException;
import java.io.OutputStream;

public class OutputStreamDelivery implements Delivery {
    private OutputStream outputStream;

    public OutputStreamDelivery(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public void deliver(Serializer serializer, Object object) {
        try {
            serializer.writeToStream(outputStream, object);
        } catch (SerializationException ex) {
            // Meh
        }
    }
}
