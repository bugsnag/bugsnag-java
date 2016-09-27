package com.bugsnag.delivery;

import com.bugsnag.serialization.Serializer;

public interface Delivery {
    /**
     * Deliver the object using the serializer.
     *
     * @param serializer the serializer to use.
     * @param object     the object to deliver.
     */
    void deliver(Serializer serializer, Object object);
}
