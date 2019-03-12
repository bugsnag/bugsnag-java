package com.bugsnag;

import com.bugsnag.callbacks.DeviceCallback;

import java.util.HashMap;
import java.util.Map;

class Diagnostics {

    private String context;
    private Map<String, Object> app;
    private Map<String, Object> device;
    private Map<String, String> user = new HashMap<String, String>();
    private MetaData metaData = new MetaData();

    Diagnostics(Configuration configuration) {
        app = getDefaultAppInfo(configuration);
        device = getDefaultDeviceInfo();
    }

    private Map<String, Object> getDefaultDeviceInfo() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("hostname", DeviceCallback.getHostnameValue());
        map.put("osName", System.getProperty("os.name"));
        map.put("osVersion", System.getProperty("os.version"));
        return map;
    }

    private Map<String, Object> getDefaultAppInfo(Configuration configuration) {
        Map<String, Object> map = new HashMap<String, Object>();

        if (configuration.releaseStage != null) {
            map.put("releaseStage", configuration.releaseStage);
        }
        if (configuration.appVersion != null) {
            map.put("version", configuration.appVersion);
        }
        return map;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public Map<String, Object> getApp() {
        return app;
    }

    public void setApp(Map<String, Object> app) {
        this.app = app;
    }

    public Map<String, Object> getDevice() {
        return device;
    }

    public void setDevice(Map<String, Object> device) {
        this.device = device;
    }

    public Map<String, String> getUser() {
        return user;
    }

    public void setUser(Map<String, String> user) {
        this.user = user;
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }
}
