package com.bugsnag.logback;

import java.net.Proxy;

public class ProxyConfiguration {
    private Proxy.Type type;
    private String hostname;
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
