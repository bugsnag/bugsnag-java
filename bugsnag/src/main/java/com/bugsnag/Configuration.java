package com.bugsnag;

import com.bugsnag.callbacks.AppCallback;
import com.bugsnag.callbacks.Callback;
import com.bugsnag.callbacks.DeviceCallback;
import com.bugsnag.callbacks.ServletCallback;
import com.bugsnag.delivery.AsyncHttpDelivery;
import com.bugsnag.delivery.Delivery;
import com.bugsnag.delivery.HttpDelivery;
import com.bugsnag.delivery.SyncHttpDelivery;
import com.bugsnag.serialization.Serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class Configuration {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bugsnag.class);

    private static final String HEADER_API_PAYLOAD_VERSION = "Bugsnag-Payload-Version";
    private static final String HEADER_API_KEY = "Bugsnag-Api-Key";
    private static final String HEADER_BUGSNAG_SENT_AT = "Bugsnag-Sent-At";

    private String apiKey;
    private String appType;
    private String appVersion;
    private Delivery delivery = new AsyncHttpDelivery(SyncHttpDelivery.DEFAULT_NOTIFY_ENDPOINT);
    private Delivery sessionDelivery =
            new AsyncHttpDelivery(SyncHttpDelivery.DEFAULT_SESSION_ENDPOINT);
    private String[] filters = new String[]{"password", "secret", "Authorization", "Cookie"};
    private String[] ignoreClasses;
    private String[] notifyReleaseStages = null;
    private String[] projectPackages;
    private String releaseStage;
    private boolean sendThreads = false;

    private Collection<Callback> callbacks = new ArrayList<Callback>();
    private Serializer serializer = new Serializer();
    private final AtomicBoolean autoCaptureSessions = new AtomicBoolean(true);
    private final AtomicBoolean sendUncaughtExceptions = new AtomicBoolean(true);

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

    public void setAutoCaptureSessions(boolean autoCaptureSessions) {
        this.autoCaptureSessions.set(autoCaptureSessions);
    }

    public boolean shouldAutoCaptureSessions() {
        return autoCaptureSessions.get();
    }

    public void setSendUncaughtExceptions(boolean sendUncaughtExceptions) {
        this.sendUncaughtExceptions.set(sendUncaughtExceptions);
    }

    public boolean shouldSendUncaughtExceptions() {
        return sendUncaughtExceptions.get();
    }

    /**
     * Set the endpoints to send data to. By default we'll send error reports to
     * https://notify.bugsnag.com, and sessions to https://sessions.bugsnag.com, but you can
     * override this if you are using Bugsnag Enterprise to point to your own Bugsnag endpoint.
     *
     * Please note that it is recommended that you set both endpoints. If the notify endpoint is
     * missing, an exception will be thrown. If the session endpoint is missing, a warning will be
     * logged and sessions will not be sent automatically.
     *
     * Note that if you are setting a custom {@link Delivery}, this method should be called after
     * the custom implementation has been set.
     *
     * @param notify the notify endpoint
     * @param sessions the sessions endpoint
     *
     * @throws IllegalArgumentException if the notify endpoint is empty or null
     */
    public void setEndpoints(String notify, String sessions) throws IllegalArgumentException {
        if (notify == null || notify.isEmpty()) {
            throw new IllegalArgumentException("Notify endpoint cannot be empty or null.");
        } else {
            if (delivery instanceof HttpDelivery) {
                ((HttpDelivery) delivery).setEndpoint(notify);
            } else {
                LOGGER.warn("Delivery is not instance of HttpDelivery, cannot set notify endpoint");
            }
        }

        boolean invalidSessionsEndpoint = sessions == null || sessions.isEmpty();
        String sessionEndpoint = null;

        if (invalidSessionsEndpoint && this.autoCaptureSessions.get()) {
            LOGGER.warn("The session tracking endpoint has not been"
                    + " set. Session tracking is disabled");
            this.autoCaptureSessions.set(false);
        } else {
            sessionEndpoint = sessions;
        }

        if (sessionDelivery instanceof HttpDelivery) {
            // sessionEndpoint may be invalid (e.g. typo in the protocol) here, which
            // should result in the default
            // HttpDelivery throwing a MalformedUrlException which prevents delivery.
            ((HttpDelivery) sessionDelivery).setEndpoint(sessionEndpoint);
        } else {
            LOGGER.warn("Delivery is not instance of HttpDelivery, cannot set sessions endpoint");
        }
    }

    Map<String, String> getErrorApiHeaders() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(HEADER_API_PAYLOAD_VERSION, Report.PAYLOAD_VERSION);
        map.put(HEADER_API_KEY, apiKey);
        map.put(HEADER_BUGSNAG_SENT_AT, DateUtils.toIso8601(new Date()));
        return map;
    }

    Map<String, String> getSessionApiHeaders() {
        Map<String, String> map = new HashMap<String, String>();
        map.put(HEADER_API_PAYLOAD_VERSION, "1.0");
        map.put(HEADER_API_KEY, apiKey);
        map.put(HEADER_BUGSNAG_SENT_AT, DateUtils.toIso8601(new Date()));
        return map;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getAppType() {
        return appType;
    }

    public void setAppType(String appType) {
        this.appType = appType;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public Delivery getDelivery() {
        return delivery;
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
    }

    public Delivery getSessionDelivery() {
        return sessionDelivery;
    }

    public void setSessionDelivery(Delivery sessionDelivery) {
        this.sessionDelivery = sessionDelivery;
    }

    public String[] getFilters() {
        return filters;
    }

    public void setFilters(String[] filters) {
        this.filters = filters;
    }

    public String[] getIgnoreClasses() {
        return ignoreClasses;
    }

    public void setIgnoreClasses(String[] ignoreClasses) {
        this.ignoreClasses = ignoreClasses;
    }

    public String[] getNotifyReleaseStages() {
        return notifyReleaseStages;
    }

    public void setNotifyReleaseStages(String[] notifyReleaseStages) {
        this.notifyReleaseStages = notifyReleaseStages;
    }

    public String[] getProjectPackages() {
        return projectPackages;
    }

    public void setProjectPackages(String[] projectPackages) {
        this.projectPackages = projectPackages;
    }

    public String getReleaseStage() {
        return releaseStage;
    }

    public void setReleaseStage(String releaseStage) {
        this.releaseStage = releaseStage;
    }

    public boolean isSendThreads() {
        return sendThreads;
    }

    public void setSendThreads(boolean sendThreads) {
        this.sendThreads = sendThreads;
    }

    public Collection<Callback> getCallbacks() {
        return callbacks;
    }

    public void setCallbacks(Collection<Callback> callbacks) {
        this.callbacks = callbacks;
    }

    public Serializer getSerializer() {
        return serializer;
    }

    public void setSerializer(Serializer serializer) {
        this.serializer = serializer;
    }

    public AtomicBoolean getAutoCaptureSessions() {
        return autoCaptureSessions;
    }

    public AtomicBoolean getSendUncaughtExceptions() {
        return sendUncaughtExceptions;
    }
}
