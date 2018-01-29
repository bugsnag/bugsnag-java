package com.bugsnag;

import com.bugsnag.callbacks.DeviceCallback;
import com.bugsnag.serialization.Expose;

import java.util.HashMap;
import java.util.Map;

class Diagnostics {

    String context;
    Map<String, Object> app;
    Map<String, Object> device;
    Map<String, String> user = new HashMap<String, String>();
    MetaData metaData = new MetaData();

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

}
