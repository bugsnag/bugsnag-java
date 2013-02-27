package com.bugsnag;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;

class Notification {
    private static final String NOTIFIER_NAME = "Java Bugsnag Notifier";
    private static final String NOTIFIER_VERSION = "2.0.0";
    private static final String NOTIFIER_URL = "https://bugsnag.com";

    private String apiKey;
    private List<Error> errorList = new ArrayList<Error>();

    public Notification(String apiKey) {
        this.apiKey = apiKey;
    }

    public Notification(String apiKey, Error error) {
        this(apiKey);
        addError(error);
    }

    public void addError(Error error) {
        errorList.add(error);
    }

    public JSONObject toJSON() {
        // Outer payload
        JSONObject notification = new JSONObject();
        Util.addToJSONObject(notification, "apiKey", apiKey);

        // Notifier info
        JSONObject notifier = new JSONObject();
        Util.addToJSONObject(notifier, "name", NOTIFIER_NAME);
        Util.addToJSONObject(notifier, "version", NOTIFIER_VERSION);
        Util.addToJSONObject(notifier, "url", NOTIFIER_URL);
        Util.addToJSONObject(notification, "notifier", notifier);

        // Error array
        JSONArray errors = new JSONArray();
        for(Error error : errorList) {
            Util.addToJSONArray(errors, error.toJSON());
        }
        Util.addToJSONObject(notification, "events", errors);

        return notification;
    }

    public String toString() {
        return toJSON().toString();
    }
}