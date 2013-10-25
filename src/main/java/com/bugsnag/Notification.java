package com.bugsnag;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Vector;
import java.util.List;
import java.util.Enumeration;
import java.io.UnsupportedEncodingException;

import org.json.JSONObject;
import org.json.JSONArray;

import com.bugsnag.http.HttpClient;
import com.bugsnag.http.NetworkException;
import com.bugsnag.utils.JSONUtils;

public class Notification {
    private Configuration config;
    ByteArrayInputStream firstNotificationStream = null;
    ByteArrayInputStream secondNotificationStream = null;
    InputStream errorStream;

    public Notification(Configuration config) {
        this.config = config;

        // Outer payload
        JSONObject notification = new JSONObject();
        JSONUtils.safePut(notification, "apiKey", config.apiKey);

        // Notifier info
        JSONObject notifier = new JSONObject();
        JSONUtils.safePut(notifier, "name", config.notifierName);
        JSONUtils.safePut(notifier, "version", config.notifierVersion);
        JSONUtils.safePut(notifier, "url", config.notifierUrl);
        JSONUtils.safePut(notification, "notifier", notifier);

        // Error array
        JSONUtils.safePut(notification, "events", new JSONArray());

        String notificationString = notification.toString();
        String eventsLocator = "events\":[";
        int eventsLocation = notificationString.indexOf(eventsLocator);
        try {
            firstNotificationStream = new ByteArrayInputStream(notificationString.substring(0, eventsLocation + eventsLocator.length()).getBytes("UTF-8"));
            secondNotificationStream = new ByteArrayInputStream(notificationString.substring(eventsLocation + eventsLocator.length()).getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            config.logger.warn("Unable to create notification stream", e);
        }
    }

    public Notification(Configuration config, Error error) {
        this(config);
        setError(error);
    }

    public void setError(Error error) {
        if(error != null) {
            try {
                errorStream = new ByteArrayInputStream(error.toString().getBytes("UTF-8"));
            } catch (UnsupportedEncodingException e) {
                config.logger.warn("Unable to stream error to bugsnag", e);
            }
        }
    }

    public void setError(File file) {
        if(file != null && file.exists() && file.isFile()) {
            try {
                errorStream = new FileInputStream(file);
            } catch (java.io.FileNotFoundException e) {
                config.logger.warn("Bugsnag error file not found, but file exists...", e);
            }
        }
    }

    public void deliver() throws NetworkException {
        if(errorStream != null && firstNotificationStream != null && secondNotificationStream != null) {
            firstNotificationStream.reset();
            secondNotificationStream.reset();

            Vector<InputStream> inputStreams = new Vector<InputStream>();
            inputStreams.add(firstNotificationStream);
            inputStreams.add(errorStream);
            inputStreams.add(secondNotificationStream);

            Enumeration<InputStream> enu = inputStreams.elements();
            SequenceInputStream sis = new SequenceInputStream(enu);

            String url = config.getNotifyEndpoint();
            HttpClient.post(url, sis, "application/json");

            config.logger.info(String.format("Sent 1 error to Bugsnag (%s)", url));

            try { sis.close(); } catch (java.io.IOException e){config.logger.warn("Unable to close stream in bugsnag", e);}
            try { errorStream.close(); } catch (java.io.IOException e){config.logger.warn("Unable to close stream in bugsnag", e);}
            errorStream = null;
        }
    }
}