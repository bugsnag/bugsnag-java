package com.bugsnag;

import org.json.JSONObject;

import com.bugsnag.http.HttpClient;
import com.bugsnag.http.NetworkException;
import com.bugsnag.utils.JSONUtils;

public class Metrics extends JSONObject {
    private Configuration config;

    public Metrics(Configuration config, String userId) {
        this.config = config;

        JSONUtils.safePut(this, "apiKey", config.apiKey);
        JSONUtils.safePut(this, "userId", userId);
    }

    public void deliver() throws NetworkException {
        String url = config.getMetricsEndpoint();
        HttpClient.post(url, this.toString(), "application/json");

        config.logger.info(String.format("Sent metrics data for user (%s) to Bugsnag (%s)", this.optString("userId"), url));
    }
}