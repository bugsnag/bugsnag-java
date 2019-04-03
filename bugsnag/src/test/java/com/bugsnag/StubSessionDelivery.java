package com.bugsnag;

import com.bugsnag.delivery.HttpDelivery;
import com.bugsnag.serialization.Serializer;

import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class to intercept data from Bugsnag for testing
 */
public class StubSessionDelivery implements HttpDelivery {

    /** The list of messages sent to Bugsnag*/
    private List<SessionPayload> sessions = new ArrayList<SessionPayload>();
    private String endpoint = null;
    private Proxy proxy = null;

    @Override
    public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
        sessions.add((SessionPayload) object);
    }

    @Override
    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public void setTimeout(int timeout) {

    }

    @Override
    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    @Override
    public void close() {

    }

    public List<SessionPayload> getSessions() {
        return sessions;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public Proxy getProxy() {
        return proxy;
    }
}
