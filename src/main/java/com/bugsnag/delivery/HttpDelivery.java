package com.bugsnag.delivery;

import java.net.Proxy;

public interface HttpDelivery extends Delivery {
    public abstract void setEndpoint(String endpoint);
    public abstract void setTimeout(int timeout);
    public abstract void setProxy(Proxy proxy);
}
