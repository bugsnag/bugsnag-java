package com.bugsnag.delivery;

import java.net.Proxy;

public interface HttpDelivery extends Delivery {
    /**
     * Set the HTTP endpoint to deliver to.
     *
     * @param endpoint the endpoint, for example https://notify.bugsnag.com
     */
    void setEndpoint(String endpoint);

    /**
     * The timeout to use.
     *
     * @param timeout the timeout for a delivery attempt.
     */
    void setTimeout(int timeout);

    /**
     * The proxy to use.
     *
     * @param proxy the proxy for the delivery attempt.
     */
    void setProxy(Proxy proxy);
}
