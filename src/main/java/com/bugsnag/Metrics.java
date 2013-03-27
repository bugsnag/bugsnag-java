package com.bugsnag;

import org.json.JSONObject;

import com.bugsnag.http.HttpClient;
import com.bugsnag.http.NetworkException;
import com.bugsnag.utils.JSONUtils;

public class Metrics {
    private Configuration config;
    private String userId;

    public Metrics(Configuration config, String userId) {
        this.config = config;
        this.userId = userId;
    }

    public void deliver() throws NetworkException {
        String url = config.getMetricsEndpoint();
        try {
            HttpClient.post(url, this.toString(), "application/json");
            config.logger.info(String.format("Sent metrics data for user (%s) to Bugsnag (%s)", userId, url));
        } catch(java.io.UnsupportedEncodingException e) {
            config.logger.warn("Bugsnag unable to send metrics", e);
        }
    }

    public JSONObject toJSON() {
        JSONObject metrics = new JSONObject();

        JSONUtils.safePut(metrics, "apiKey", config.apiKey);
        JSONUtils.safePut(metrics, "userId", userId);

        return metrics;
    }

    public String toString() {
        return toJSON().toString();
    }
}