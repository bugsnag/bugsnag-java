package com.bugsnag;

import com.bugsnag.delivery.Delivery;
import com.bugsnag.serialization.Serializer;

class BugsnagTestUtils {

    static Delivery generateDelivery() {
        return new Delivery() {
            @Override
            public void deliver(Serializer serializer, Object object) {

            }

            @Override
            public void close() {

            }
        };
    }
}
