package com.bugsnag.delivery;

import com.bugsnag.serialization.Serializer;

public interface Delivery {
    public abstract void deliver(Serializer serializer, Object object);
}
