package com.bugsnag;

import com.bugsnag.callbacks.AppCallback;
import com.bugsnag.callbacks.Callback;
import com.bugsnag.callbacks.DeviceCallback;
import com.bugsnag.callbacks.JakartaServletCallback;
import com.bugsnag.callbacks.JavaxServletCallback;
import com.bugsnag.delivery.AsyncHttpDelivery;
import com.bugsnag.delivery.Delivery;
import com.bugsnag.delivery.HttpDelivery;
import com.bugsnag.delivery.SyncHttpDelivery;
import com.bugsnag.serialization.Serializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("visibilitymodifier")
public class Configuration {

    private static final Logger LOGGER = LoggerFactory.getLogger(Bugsnag.class);

    private static final String HEADER_API_PAYLOAD_VERSION = "Bugsnag-Payload-Version";
    private static final String HEADER_API_KEY = "Bugsnag-Api-Key";
    private static final String HEADER_BUGSNAG_SENT_AT = "Bugsnag-Sent-At";

    public String apiKey;
    public String appType;
    public String appVersion;
    public Delivery delivery = new AsyncHttpDelivery(SyncHttpDelivery.DEFAULT_NOTIFY_ENDPOINT);
    public Delivery sessionDelivery =
            new AsyncHttpDelivery(SyncHttpDelivery.DEFAULT_SESSION_ENDPOINT);
    public String[] filters = new String[]{"ipAddress", "logLevel"};
    public String[] redactedKeys = new String[]{"password", "secret", "Authorization", "Cookie"};
    public String[] ignoreClasses;
    public String[] notifyReleaseStages = null;
    public String[] projectPackages;
    public String releaseStage;
    public boolean sendThreads = false;

    Collection<Callback> callbacks = new ConcurrentLinkedQueue<Callback>();
    Serializer serializer = new Serializer();
    private final AtomicBoolean autoCaptureSessions = new AtomicBoolean(true);
    private final AtomicBoolean sendUncaughtExceptions = new AtomicBoolean(true);

    Configuration(String apiKey) {
        this.apiKey = apiKey;

        // Add built-in callbacks
        addCallback(new AppCallback(this));
        addCallback(new DeviceCallback());
        DeviceCallback.initializeCache();

        if (JavaxServletCallback.isAvailable()) {
            addCallback(new JavaxServletCallback());
        }

        if (JakartaServletCallback.isAvailable()) {
            addCallback(new JakartaServletCallback());
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
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }
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
}
