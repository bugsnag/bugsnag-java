package com.bugsnag;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;

import com.bugsnag.http.HttpClient;
import com.bugsnag.http.NetworkException;
import com.bugsnag.utils.JSONUtils;

public class Notification extends JSONObject {
    private Configuration config;
    private int stringEventCount;
    private StringBuilder stringEvents;

    public Notification(Configuration config) {
        this.config = config;
        this.stringEvents = new StringBuilder();
        this.stringEventCount = 0;

        // Outer payload
        JSONUtils.safePut(this, "apiKey", config.apiKey);

        // Notifier info
        JSONObject notifier = new JSONObject();
        JSONUtils.safePut(notifier, "name", config.notifierName);
        JSONUtils.safePut(notifier, "version", config.notifierVersion);
        JSONUtils.safePut(notifier, "url", config.notifierUrl);
        JSONUtils.safePut(this, "notifier", notifier);

        // Add empty events array as required for later
        JSONUtils.safePut(this, "events", new JSONArray());
    }

    public Notification(Configuration config, Error error) {
        this(config);
        addError(error);
    }

    public void addError(Error error) {
        events().put(error);
    }

    public void addError(String errorString) {
        if(stringEventCount > 0) stringEvents.append(",");
        stringEventCount++;
        stringEvents.append(errorString);
    }

    public void deliver() throws NetworkException {
        if(size() == 0) return;

        String url = config.getNotifyEndpoint();
        HttpClient.post(url, this.toString(), "application/json");

        config.logger.info(String.format("Sent %d error(s) to Bugsnag (%s)", size(), url));
    }

    public String toString() {
        String returnValue = super.toString();

        if(stringEventCount > 0) {
            int eventArrayLocation = returnValue.indexOf("events:[");
            StringBuilder returnString = new StringBuilder()
                .append(returnValue.substring(0, eventArrayLocation + 8))
                .append(stringEvents)
                .append(events().length() > 0 ? "," : "")
                .append(returnValue.substring(eventArrayLocation + 8));

            return returnString.toString();
        } else {
            return returnValue;
        }
    }

    public int size() {
        return events().length() + stringEventCount;
    }

    protected JSONArray events() {
        JSONArray events = this.optJSONArray("events");
        if(events == null) {
            events = new JSONArray();
            JSONUtils.safePut(this, "events", events);
        }
        return events;
    }
}