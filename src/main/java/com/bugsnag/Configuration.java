package com.bugsnag;

import com.bugsnag.callbacks.AppCallback;
import com.bugsnag.callbacks.Callback;
import com.bugsnag.callbacks.DeviceCallback;
import com.bugsnag.callbacks.ServletCallback;
import com.bugsnag.delivery.AsyncHttpDelivery;
import com.bugsnag.delivery.Delivery;
import com.bugsnag.delivery.HttpDelivery;
import com.bugsnag.serialization.Serializer;

import java.util.*;

public class Configuration {

    private static final String HEADER_API_PAYLOAD_VERSION = "Bugsnag-Payload-Version";
    private static final String HEADER_API_KEY = "Bugsnag-Api-Key";
    private static final String HEADER_BUGSNAG_SENT_AT = "Bugsnag-Sent-At";

    public String apiKey;
    public String appType;
    public String appVersion;
    public Delivery delivery = new AsyncHttpDelivery();
    public Delivery sessionDelivery;
    public String[] filters = new String[]{"password"};
    public String[] ignoreClasses;
    public String[] notifyReleaseStages = null;
    public String[] projectPackages;
    public String releaseStage;
    public boolean sendThreads = false;

    Collection<Callback> callbacks = new ArrayList<Callback>();
    Serializer serializer = new Serializer();
    private volatile boolean autoCaptureSessions;

    Configuration(String apiKey) {
        this.apiKey = apiKey;

        // Add built-in callbacks
        addCallback(new AppCallback(this));
        addCallback(new DeviceCallback());
        DeviceCallback.initializeCache();

        if (ServletCallback.isAvailable()) {
            addCallback(new ServletCallback());
        }
        sessionDelivery = new AsyncHttpDelivery();
        ((HttpDelivery) sessionDelivery).setEndpoint("https://sessions.bugsnag.com");
    }

    boolean shouldNotifyForReleaseStage() {
        if (notifyReleaseStages == null) {
            return true;
        }

        List<String> stages = Arrays.asList(notifyReleaseStages);
        return stages.contains(releaseStage);
    }

    boolean shouldIgnoreClass(String className) {
        if (ignoreClasses == null) {
            return false;
        }

        List<String> classes = Arrays.asList(ignoreClasses);
        return classes.contains(className);
    }

    void addCallback(Callback callback) {
        callbacks.add(callback);
    }

    boolean inProject(String className) {
        if (projectPackages != null) {
            for (String packageName : projectPackages) {
                if (packageName != null && className.startsWith(packageName)) {
                    return true;
                }
            }
        }

        return false;
    }

    public void setAutoCaptureSessions(boolean autoCaptureSessions) {
        this.autoCaptureSessions = autoCaptureSessions;
    }

    public boolean shouldAutoCaptureSessions() {
        return autoCaptureSessions;
    }

    Map<String, String> getErrorApiHeaders() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(HEADER_API_PAYLOAD_VERSION, "4.0");
        map.put(HEADER_API_KEY, apiKey);
        map.put(HEADER_BUGSNAG_SENT_AT, DateUtils.toISO8601(new Date()));
        return map;
    }

    Map<String, String> getSessionApiHeaders() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(HEADER_API_PAYLOAD_VERSION, "1.0");
        map.put(HEADER_API_KEY, apiKey);
        map.put(HEADER_BUGSNAG_SENT_AT, DateUtils.toISO8601(new Date()));
        return map;
    }
}
