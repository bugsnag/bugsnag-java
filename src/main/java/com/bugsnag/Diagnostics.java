package com.bugsnag;

import com.bugsnag.utils.JSONUtils;
import org.json.JSONObject;

public class Diagnostics {
    protected Configuration config;
    protected JSONObject deviceData = new JSONObject();
    protected JSONObject appData = new JSONObject();

    public Diagnostics(Configuration config) {
        this.config = config;

        JSONUtils.safePutOpt(deviceData, "osName", System.getProperty("os.name"));
        JSONUtils.safePutOpt(deviceData, "hostname", getHostname());
    }

    public JSONObject getAppData() {
        JSONUtils.safePutOpt(appData, "version", config.appVersion.get());
        JSONUtils.safePutOpt(appData, "releaseStage", config.releaseStage.get());
        return appData;
    }

    public JSONObject getAppState() {
        return new JSONObject();
    }

    public JSONObject getDeviceData() {
        JSONUtils.safePutOpt(deviceData, "osVersion", config.osVersion.get());
        return deviceData;
    }

    public JSONObject getDeviceState() {
        return new JSONObject();
    }

    public String getContext() {
        return config.context.get();
    }

    public JSONObject getUser() {
        return config.user;
    }

    public JSONObject getMetrics() {
        JSONObject metrics = new JSONObject();

        JSONUtils.safePutOpt(metrics, "user", getUser());
        JSONUtils.safePutOpt(metrics, "app", getAppData());
        JSONUtils.safePutOpt(metrics, "device", getDeviceData());

        return metrics;
    }

    private String getHostname() {
        String hostname = null;

        // Try get the hostname from the environment (since using InetAddress.getLocalHost() can cause a buffer overflow on openjdk)
        try {
            hostname = System.getenv("HOSTNAME"); // Unix/Linux
            if (hostname == null) {
                hostname = System.getenv("COMPUTERNAME"); // Windows
            }
        } catch (SecurityException e) {
            config.logger.warn("Unable to obtain hostname for Bugsnag diagnostics", e);
        }

        if (hostname != null) {
            hostname = hostname.trim();
        }

        return hostname;
    }
}