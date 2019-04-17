package com.bugsnag;

import com.bugsnag.callbacks.DeviceCallback;

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
        map.put("runtimeVersions", getRuntimeVersions());
        return map;
    }

    private Map<String, String> getRuntimeVersions() {
        Map<String, String> runtimeVersions = new HashMap<String, String>();
        runtimeVersions.put("javaType", System.getProperty("java.runtime.name"));
        runtimeVersions.put("javaVersion", System.getProperty("java.runtime.version"));
        return runtimeVersions;
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

    @SuppressWarnings("unchecked")
    static Map<String, Object> retrieveRuntimeVersionsMap(Map<String, Object> device) {
        Object obj = device.get("runtimeVersions");

        if (obj instanceof Map) {
            return (Map<String, Object>) obj;
        } else { // fallback to creating a new map if payload was mutated
            Map<String, Object> map = new HashMap<String, Object>();
            device.put("runtimeVersions", map);
            return map;
        }
    }
}
