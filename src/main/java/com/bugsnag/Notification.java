package com.bugsnag;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;

public class Notification {
    private static final String NOTIFIER_NAME = "Java Bugsnag Notifier";
    private static final String NOTIFIER_VERSION = "2.0.0";
    private static final String NOTIFIER_URL = "https://bugsnag.com";

    private Configuration config;
    private List<Error> errorList = new ArrayList<Error>();

    public Notification(Configuration config) {
        this.config = config;
    }

    public Notification(Configuration config, Error error) {
        this(config);
        addError(error);
    }

    public void addError(Error error) {
        errorList.add(error);
    }

    public JSONObject toJSON() {
        // Outer payload
        JSONObject notification = new JSONObject();
        Util.addToJSONObject(notification, "apiKey", config.getApiKey());

        // Notifier info
        JSONObject notifier = new JSONObject();
        Util.addToJSONObject(notifier, "name", NOTIFIER_NAME);
        Util.addToJSONObject(notifier, "version", NOTIFIER_VERSION);
        Util.addToJSONObject(notifier, "url", NOTIFIER_URL);
        Util.addToJSONObject(notification, "notifier", notifier);

        // Error array
        JSONArray errors = new JSONArray();
        for(Error error : errorList) {
            errors.put(error.toJSON());
        }
        Util.addToJSONObject(notification, "events", errors);

        return notification;
    }

    public String toString() {
        return toJSON().toString();
    }

    public boolean deliver() {
        String url = config.getEndpoint();
        boolean sent = request(url, this.toString(), "application/json");
        if(sent) {
            config.getLogger().info(String.format("Sent error(s) to %s", url));
        }

        return sent;
    }

    private boolean request(String urlString, String payload, String contentType) {
        return request(urlString, Util.stringToByteArray(payload), contentType);
    }

    private boolean request(String urlString, byte[] payload, String contentType) {
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
                config.getLogger().warn(String.format("Got non-200 response code from %s: %d", urlString, status));
            }

            // The request was sent if we didn't have an exception
            sent = true;
        } catch (IOException e) {
            config.getLogger().warn("Connection error when making request to " + urlString, e);
        } finally {
            if(conn != null) {
                conn.disconnect();
            }
        }

        return sent;
    }
}