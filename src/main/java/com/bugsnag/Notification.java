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
    private List<Error> errorList = new ArrayList<Error>();
    private List<String> errorStrings = new ArrayList<String>();

    public Notification(Configuration config) {
        this.config = config;

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
        errorList.add(error);

        events().put(error);
    }

    public void addError(String errorString) {
        try {
            events().put(new JSONObject(errorString));
        } catch(JSONException e) {
            config.logger.warn("Error when parsing error json string", e);
        }
    }

    public void deliver() throws NetworkException {
        if(errorList.isEmpty() && errorStrings.isEmpty())
            return;

        String url = config.getNotifyEndpoint();
        HttpClient.post(url, this.toString(), "application/json");

        config.logger.info(String.format("Sent %d error(s) to Bugsnag (%s)", size(), url));
    }

    public int size() {
        return events().length();
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