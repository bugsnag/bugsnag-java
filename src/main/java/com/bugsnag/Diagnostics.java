package com.bugsnag;

import org.json.JSONObject;

import com.bugsnag.utils.JSONUtils;

public class Diagnostics {
    protected Configuration config;

    public Diagnostics(Configuration config) {
        this.config = config;
    }

    public JSONObject getApp() {
        JSONObject appData = new JSONObject();
        JSONUtils.safePutNotNull(appData, "version", config.appVersion);
        JSONUtils.safePutNotNull(appData, "releaseStage", config.releaseStage);
        return appData;
    }

    public JSONObject getAppState() {
        return null;
    }

    public JSONObject getHost() {
        JSONObject hostData = new JSONObject();
        JSONUtils.safePutNotNull(hostData, "osVersion", config.osVersion);
        return hostData;
    }

    public JSONObject getHostState() {
        return null;
    }
}