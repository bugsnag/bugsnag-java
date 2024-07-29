package com.bugsnag.delivery;

import com.bugsnag.serialization.Serializer;

import java.util.Map;

public interface Delivery {
    /**
     * Deliver the object using the serializer.
     *
     * @param serializer the serializer to use.
     * @param object     the object to deliver.
     * @param headers    HTTP headers which must be appended to the request.
     */
    void deliver(Serializer serializer, Object object, Map<String, String> headers);

    /**
     * Close any open connections to Bugsnag.
     */
    void close();
}
