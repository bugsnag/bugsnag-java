package com.bugsnag;

import com.bugsnag.delivery.Delivery;
import com.bugsnag.serialization.Serializer;

import java.util.Map;

class BugsnagTestUtils {

    static Delivery generateDelivery() {
        return new Delivery() {
            @Override
            public void deliver(Serializer serializer, Object object, Map<String, String> headers) {

            }

            @Override
            public void close() {

            }
        };
    }
}
