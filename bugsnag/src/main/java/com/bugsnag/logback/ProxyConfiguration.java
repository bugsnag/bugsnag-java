package com.bugsnag.logback;

import java.net.Proxy;

/** Proxy configuration. */
public class ProxyConfiguration {
    /** Proxy type. */
    private Proxy.Type type;

    /** Hostname. */
    private String hostname;

    /** Port number. */
    private int port;

    public Proxy.Type getType() {
        return type;
    }

    public void setType(Proxy.Type type) {
        this.type = type;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
