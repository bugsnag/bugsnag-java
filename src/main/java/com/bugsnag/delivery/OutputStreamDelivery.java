package com.bugsnag.delivery;

import com.bugsnag.serialization.SerializationException;
import com.bugsnag.serialization.Serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;

public class OutputStreamDelivery implements Delivery {
    private static final Logger logger = LoggerFactory.getLogger(OutputStreamDelivery.class);
    private OutputStream outputStream;

    public OutputStreamDelivery(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void deliver(Serializer serializer, Object object) {
        try {
            serializer.writeToStream(outputStream, object);
        } catch (SerializationException ex) {
            logger.warn("Error not reported to Bugsnag - exception when serializing payload", ex);
        }
    }

    @Override
    public void close() {
        // Nothing to do here.
    }
}
