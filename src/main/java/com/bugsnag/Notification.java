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

public class Notification {
    private Configuration config;
    private List<Error> errorList = new ArrayList<Error>();
    private List<String> errorStrings = new ArrayList<String>();

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

    public void addError(String errorString) {
        errorStrings.add(errorString);
    }

    public JSONObject toJSON() {
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
        JSONArray errors = new JSONArray();
        for(Error error : errorList) {
            errors.put(error.toJSON());
        }
        for(String errorString : errorStrings) {
            try {
                JSONObject error = new JSONObject(errorString);
                errors.put(error);
            } catch(JSONException e) {
                config.logger.warn("Error when parsing error json string", e);
            }
        }
        JSONUtils.safePut(notification, "events", errors);

        return notification;
    }

    public String toString() {
        return toJSON().toString();
    }

    public void deliver() throws NetworkException {
        if(errorList.isEmpty() && errorStrings.isEmpty())
            return;

        String url = config.getNotifyEndpoint();
        HttpClient.post(url, this.toString(), "application/json");

        config.logger.info(String.format("Sent %d error(s) to Bugsnag (%s)", size(), url));
    }

    public int size() {
        return errorList.size() + errorStrings.size();
    }
}