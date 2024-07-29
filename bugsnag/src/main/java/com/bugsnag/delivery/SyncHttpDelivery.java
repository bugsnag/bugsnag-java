package com.bugsnag.delivery;

import com.bugsnag.serialization.Serializer;
import com.bugsnag.serialization.SerializationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Map;

public class SyncHttpDelivery implements HttpDelivery {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncHttpDelivery.class);

    public static final String DEFAULT_NOTIFY_ENDPOINT = "https://notify.bugsnag.com";
    public static final String DEFAULT_SESSION_ENDPOINT = "https://sessions.bugsnag.com";
    protected static final int DEFAULT_TIMEOUT = 5000;

    protected String endpoint;
    protected int timeout = DEFAULT_TIMEOUT;
    protected Proxy proxy;

    /**
     * Creates a new instance, which defaults to the https://notify.bugsnag.com endpoint
     */
    public SyncHttpDelivery() {
        this(SyncHttpDelivery.DEFAULT_NOTIFY_ENDPOINT);
    }

    /**
     * Creates a new instance, which uses a custom endpoint
     */
    public SyncHttpDelivery(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setProxy(Proxy proxy) {
        this.proxy = proxy;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public void deliver(Serializer serializer, Object object, Map<String, String> headers) {
        if (endpoint == null) {
            LOGGER.warn("Endpoint configured incorrectly, skipping delivery.");
            return;
        }

        HttpURLConnection connection = null;
        try {
            URL url = new URL(endpoint);
            if (proxy != null) {
                connection = (HttpURLConnection) url.openConnection(proxy);
            } else {
                connection = (HttpURLConnection) url.openConnection();
            }

            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(timeout);
            connection.addRequestProperty("Content-Type", "application/json");

            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.addRequestProperty(entry.getKey(), entry.getValue());
            }

            OutputStream outputStream = null;
            try {
                outputStream = connection.getOutputStream();
                serializer.writeToStream(outputStream, object);
            } finally {
                try {
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (final IOException ioe) {
                    // Don't care
                }
            }

            // End the request, get the response code
            int status = connection.getResponseCode();
            if (status / 100 != 2) {
                LOGGER.warn(
                        "Error not reported to Bugsnag - got non-200 response code: {}", status);
            }
        } catch (MalformedURLException ex) {
            LOGGER.warn("Error not reported to Bugsnag - malformed URL."
                    + " Have you set both endpoints correctly?", ex);
        } catch (SerializationException ex) {
            LOGGER.warn("Error not reported to Bugsnag - exception when serializing payload", ex);
        } catch (UnknownHostException ex) {
            LOGGER.warn("Error not reported to Bugsnag - unknown host {}", endpoint);
        } catch (IOException ex) {
            LOGGER.warn("Error not reported to Bugsnag - exception when making request", ex);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    @Override
    public void close() {
        // Nothing to do here.
    }
}
