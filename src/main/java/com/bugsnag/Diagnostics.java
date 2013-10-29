package com.bugsnag;

import org.json.JSONObject;
import org.json.JSONException;

import com.bugsnag.utils.JSONUtils;

public class Diagnostics {
    protected Configuration config;
    protected JSONObject hostData;
    protected JSONObject appData;

    public Diagnostics(Configuration config) {
        this.config = config;
        
        hostData = new JSONObject();
        appData = new JSONObject();
    }

    public JSONObject getAppData() {
        JSONUtils.safePutOpt(appData, "version", config.appVersion);
        JSONUtils.safePutOpt(appData, "releaseStage", config.releaseStage);
        return appData;
    }

    public JSONObject getAppState() {
        return new JSONObject();
    }

    public JSONObject getHostData() {
        JSONUtils.safePutOpt(hostData, "osVersion", config.osVersion);
        return hostData;
    }

    public JSONObject getHostState() {
        return new JSONObject();
    }

    public String getContext() {
        return config.context;
    }

    public JSONObject getMetrics() {
        JSONObject metrics = new JSONObject();

        JSONUtils.safePutOpt(metrics, "userId", config.user.optString("id"));
        JSONUtils.safePutOpt(metrics, "user", config.user);
        JSONUtils.safePutOpt(metrics, "app", this.getAppData());
        JSONUtils.safePutOpt(metrics, "host", this.getHostData());

        return metrics;
    }
}