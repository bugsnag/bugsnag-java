package com.bugsnag;

import org.json.JSONObject;
import org.json.JSONException;

import com.bugsnag.utils.JSONUtils;

public class Diagnostics {
    protected Configuration config;
    protected JSONObject hostData = new JSONObject();
    protected JSONObject appData = new JSONObject();

    public Diagnostics(Configuration config) {
        this.config = config;
    }

    public JSONObject getAppData() {
        JSONUtils.safePutOpt(appData, "version", config.appVersion.get());
        JSONUtils.safePutOpt(appData, "releaseStage", config.releaseStage.get());
        return appData;
    }

    public JSONObject getAppState() {
        return new JSONObject();
    }

    public JSONObject getHostData() {
        JSONUtils.safePutOpt(hostData, "osVersion", config.osVersion.get());
        return hostData;
    }

    public JSONObject getHostState() {
        return new JSONObject();
    }

    public String getContext() {
        return config.context.get();
    }

    public JSONObject getMetrics() {
        JSONObject metrics = new JSONObject();

        JSONUtils.safePutOpt(metrics, "user", config.user);
        JSONUtils.safePutOpt(metrics, "app", this.getAppData());
        JSONUtils.safePutOpt(metrics, "host", this.getHostData());

        return metrics;
    }
}