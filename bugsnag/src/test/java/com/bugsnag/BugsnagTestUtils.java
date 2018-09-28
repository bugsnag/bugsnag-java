package com.bugsnag;

import com.bugsnag.delivery.Delivery;
import com.bugsnag.serialization.Serializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
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

    static JsonNode mapReportToJson(Configuration config, Report report) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Notification notification = new Notification(config, report);
        String json = mapper.writeValueAsString(notification);
        return mapper.readTree(json);
    }

    static JsonNode mapSessionPayloadToJson(SessionPayload sessionPayload) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(sessionPayload);
        return mapper.readTree(json);
    }
}
