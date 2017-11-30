package com.bugsnag;

import com.bugsnag.callbacks.AppCallback;
import com.bugsnag.callbacks.Callback;
import com.bugsnag.callbacks.DeviceCallback;
import com.bugsnag.callbacks.ServletCallback;
import com.bugsnag.delivery.AsyncHttpDelivery;
import com.bugsnag.delivery.Delivery;
import com.bugsnag.serialization.Serializer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class Configuration {
    public String apiKey;
    public String appType;
    public String appVersion;
    public Delivery delivery = new AsyncHttpDelivery();
    public String[] filters = new String[]{"password"};
    public String[] ignoreClasses;
    public String[] notifyReleaseStages = null;
    public String[] projectPackages;
    public String releaseStage;
    public boolean sendThreads = false;

    Collection<Callback> callbacks = new ArrayList<Callback>();
    Serializer serializer = new Serializer();

    Configuration(String apiKey) {
        this.apiKey = apiKey;

        // Add built-in callbacks
        addCallback(new AppCallback(this));
        addCallback(new DeviceCallback());
        DeviceCallback.initializeCache();

        if (ServletCallback.isAvailable()) {
            addCallback(new ServletCallback());
        }
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
}
