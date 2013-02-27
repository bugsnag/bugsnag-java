package com.bugsnag;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

public class Client {
    private static final String DEFAULT_ENDPOINT = "notify.bugsnag.com";

    private String apiKey;
    private boolean autoNotify = true;
    private String endpoint = DEFAULT_ENDPOINT;
    private boolean useSSL = false;
    private Configuration config = new Configuration();

    public Client(String apiKey) {
        if(apiKey == null) {
            throw new RuntimeException("You must provide a Bugsnag API key");
        }
        this.apiKey = apiKey;

        // Install a default exception handler with this client
        ExceptionHandler.install(this);
    }

    public void setContext(String context) {
        config.setContext(context);
    }

    public void setUserId(String userId) {
        config.setUserId(userId);
    }

    public void setReleaseStage(String releaseStage) {
        config.setReleaseStage(releaseStage);
    }

    public void setNotifyReleaseStages(String... notifyReleaseStages) {
        config.setNotifyReleaseStages(notifyReleaseStages);
    }

    public void setAutoNotify(boolean autoNotify) {
        this.autoNotify = autoNotify;
    }

    public void setUseSSL(boolean useSSL) {
        this.useSSL = useSSL;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public void setFilters(String... filters) {
        config.setFilters(filters);
    }

    public void setProjectPackages(String... packages) {
        config.setProjectPackages(packages);
    }

    public void notify(Throwable e, Map<String, Object> metaData) {
        if(!Arrays.asList(config.getNotifyReleaseStages()).contains(config.getReleaseStage()))
            return;

        String url = (this.useSSL ? "https://" : "http://") + this.endpoint;
        Notification notif = new Notification(apiKey, new Error(e, metaData, config));
        boolean sent = request(url, notif.toString(), "application/json");
        if(sent) {
            config.getLogger().info(String.format("Sent %s to %s", e.getClass().getName(), url));
        }
    }

    public void notify(Throwable e) {
        notify(e, null);
    }

    public void autoNotify(Throwable e) {
        if(this.autoNotify) 
            notify(e);
    }

    public void addToTab(String tab, String key, Object value) {
        config.addToTab(tab, key, value);
    }

    public void clearTab(String tab) {
        config.clearTab(tab);
    }

    protected boolean request(String urlString, String payload, String contentType) {
        return request(urlString, Util.stringToByteArray(payload), contentType);
    }

    protected boolean request(String urlString, byte[] payload, String contentType) {
        boolean sent = false;
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true); 
            conn.setFixedLengthStreamingMode(payload.length);

            // Set the content type header
            if(contentType != null) {
                conn.addRequestProperty("Content-Type", contentType);
            }

            // Send request headers and body
            conn.getOutputStream().write(payload);

            // End the request, get the response code
            int status = conn.getResponseCode();
            if(status / 100 != 2) {
                config.getLogger().warn(String.format("Got non-200 response code from %s: %d", url, status));
            }

            // The request was sent if we didn't have an exception
            sent = true;
        } catch (IOException e) {
            e.printStackTrace(System.err);
        } finally {
            if(conn != null) {
                conn.disconnect();
            }
        }

        return sent;
    }
}