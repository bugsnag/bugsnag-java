package com.bugsnag;

import com.bugsnag.callbacks.OnErrorCallback;
import com.bugsnag.delivery.Delivery;
import com.bugsnag.delivery.HttpDelivery;
import com.bugsnag.util.DaemonThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.Proxy;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class Bugsnag implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bugsnag.class);
    private static final int SHUTDOWN_TIMEOUT_MS = 5000;
    private static final int SESSION_TRACKING_PERIOD_MS = 60000;
    private static final int CORE_POOL_SIZE = 1;

    private final ThreadFactory sessionThreadFactory = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = Executors.defaultThreadFactory().newThread(runnable);
            thread.setName("bugsnag-sessions-" + thread.getId());
            return thread;
        }
    };
    // Create an executor service which keeps idle threads alive for a maximum of SHUTDOWN_TIMEOUT.
    // This should avoid blocking an application that doesn't call shutdown from exiting.
    private ExecutorService sessionFlusherService =
            new ThreadPoolExecutor(0, 1,
                    SHUTDOWN_TIMEOUT_MS, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(),
                    sessionThreadFactory);

    private ScheduledThreadPoolExecutor sessionExecutorService =
            new ScheduledThreadPoolExecutor(CORE_POOL_SIZE,
            new DaemonThreadFactory(),
            new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
                    LOGGER.error("Rejected execution for sessionExecutorService");
                }
            });

    private Configuration config;
    private final SessionTracker sessionTracker;
    private final FeatureFlagStore featureFlagStore;

    private static final ThreadLocal<Metadata> THREAD_METADATA = new ThreadLocal<Metadata>() {
        @Override
        public Metadata initialValue() {
            return new Metadata();
        }
    };

    //
    // Constructors
    //

    /**
     * Initialize a Bugsnag client and automatically send uncaught exceptions.
     *
     * @param apiKey your Bugsnag API key from your Bugsnag dashboard
     */
    public Bugsnag(String apiKey) {
        this(apiKey, true);
    }

    /**
     * Initialize a Bugsnag client.
     *
     * @param apiKey                 your Bugsnag API key
     * @param sendUncaughtExceptions should we send uncaught exceptions to Bugsnag
     */
    public Bugsnag(String apiKey, boolean sendUncaughtExceptions) {
        if (apiKey == null) {
            throw new NullPointerException("You must provide a Bugsnag API key");
        }

        config = new Configuration(apiKey);
        sessionTracker = new SessionTracker(config);
        featureFlagStore = config.copyFeatureFlagStore();

        // Automatically send unhandled exceptions to Bugsnag using this Bugsnag
        config.setSendUncaughtExceptions(sendUncaughtExceptions);
        if (sendUncaughtExceptions) {
            ExceptionHandler.enable(this);
        }
        addSessionTrackingShutdownHook();
        scheduleSessionFlushes();
    }

    private void scheduleSessionFlushes() {
        sessionExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // Use a different thread which is not a daemon thread
                // to actually flush the sessions
                sessionFlusherService.submit(new Runnable() {
                    @Override
                    public void run() {
                        sessionTracker.flushSessions(new Date());
                    }
                });
            }
        }, SESSION_TRACKING_PERIOD_MS, SESSION_TRACKING_PERIOD_MS, TimeUnit.MILLISECONDS);
    }

    private void addSessionTrackingShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                close();
            }
        });
    }

    //
    // Configuration
    //

    /**
     * Add a callback to execute code before/after every notification to Bugsnag.
     *
     * <p>You can use this to add or modify information attached to an error
     * before it is sent to your dashboard. You can also stop any reports being
     * sent to Bugsnag completely.
     *
     * @param callback a callback to run before sending errors to Bugsnag
     * @see OnErrorCallback
     */
    public void addOnError(OnErrorCallback callback) {
        config.addOnError(callback);
    }

    /**
     * Get the delivery to use to send reports.
     *
     * @return the delivery to use to send reports.
     * @see Delivery
     */
    public Delivery getDelivery() {
        return config.getDelivery();
    }

    /**
     * Get the delivery to use to send sessions.
     *
     * @return the delivery to use to send sessions.
     * @see Delivery
     */
    public Delivery getSessionDelivery() {
        return config.getSessionDelivery();
    }

    /**
     * Set the application type sent to Bugsnag.
     *
     * @param appType the app type to send, eg. spring, gradleTask
     */
    public void setAppType(String appType) {
        config.setAppType(appType);
    }

    /**
     * Set the application version sent to Bugsnag.
     *
     * @param appVersion the app version to send
     */
    public void setAppVersion(String appVersion) {
        config.setAppVersion(appVersion);
    }

    /**
     * Set the method of delivery for Bugsnag error report. By default we'll
     * send reports asynchronously using a thread pool to
     * https://notify.bugsnag.com, but you can override this to use a
     * different sending technique or endpoint (for example, if you are using
     * Bugsnag On-Premise).
     *
     * @param delivery the delivery mechanism to use
     * @see Delivery
     */
    public void setDelivery(Delivery delivery) {
        config.setDelivery(delivery);
    }

    /**
     * Set the method of delivery for Bugsnag sessions. By default we'll
     * send sessions asynchronously using a thread pool to
     * https://sessions.bugsnag.com, but you can override this to use a
     * different sending technique or endpoint (for example, if you are using
     * Bugsnag On-Premise).
     *
     * @param delivery the delivery mechanism to use
     * @see Delivery
     */
    public void setSessionDelivery(Delivery delivery) {
        config.setSessionDelivery(delivery);
    }

    /**
     * Set the endpoint to deliver Bugsnag errors report to. This is a convenient
     * shorthand for bugsnag.getDelivery().setEndpoint();
     *
     * @param endpoint the endpoint to send reports to
     * @see #setDelivery
     * @deprecated use {@link Configuration#setEndpoints(String, String)} instead
     */
    @Deprecated
    public void setEndpoint(String endpoint) {
        Delivery delivery = config.getDelivery();
        if (delivery instanceof HttpDelivery) {
            ((HttpDelivery) delivery).setEndpoint(endpoint);
        }
    }

    /**
     * Set which keys should be redacted when sending metadata to Bugsnag.
     * Use this when you want to ensure sensitive information, such as passwords
     * or credit card information is stripped from metadata you send to Bugsnag.
     * Any keys in metadata which contain these strings will be marked as
     * [REDACTED] when send to Bugsnag.
     *
     * @param redactedKeys a list of String keys to redact from metadata
     */
    public void setRedactedKeys(String... redactedKeys) {
        config.setRedactedKeys(redactedKeys);
    }

    /**
     * Set which exception classes should be ignored (not sent) by Bugsnag.
     * Uses Java regex patterns for matching exception class names.
     *
     * @param discardClasses compiled regex patterns to match exception class names
     */
    public void setDiscardClasses(Pattern... discardClasses) {
        config.setDiscardClasses(discardClasses);
    }

    /**
     * Set for which releaseStages errors should be sent to Bugsnag.
     * Use this to stop errors from development builds being sent.
     *
     * @param enabledReleaseStages a list of releaseStages to notify for
     * @see #setReleaseStage
     */
    public void setEnabledReleaseStages(String... enabledReleaseStages) {
        if (enabledReleaseStages == null || enabledReleaseStages.length == 0) {
            config.setEnabledReleaseStages(Collections.emptySet());
        } else {
            config.setEnabledReleaseStages(Set.of(enabledReleaseStages));
        }
    }

    /**
     * Set which packages should be considered part of your application.
     * Bugsnag uses this to help with error grouping, and stacktrace display.
     *
     * @param projectPackages a list of package names
     */
    public void setProjectPackages(String... projectPackages) {
        config.setProjectPackages(projectPackages);
    }

    /**
     * Set a proxy to use when delivering Bugsnag error reports and sessions. This
     * is a convenient
     * shorthand for bugsnag.getDelivery().setProxy();
     *
     * @param proxy the proxy to use to send reports
     */
    public void setProxy(Proxy proxy) {
        Delivery delivery = config.getDelivery();
        if (delivery instanceof HttpDelivery) {
            ((HttpDelivery) delivery).setProxy(proxy);
        }

        Delivery sessionDelivery = config.getSessionDelivery();
        if (sessionDelivery instanceof HttpDelivery) {
            ((HttpDelivery) sessionDelivery).setProxy(proxy);
        }
    }

    /**
     * Set the current "release stage" of your application.
     *
     * @param releaseStage the release stage of the app
     * @see #setEnabledReleaseStages
     */
    public void setReleaseStage(String releaseStage) {
        config.setReleaseStage(releaseStage);
    }

    /**
     * Set when Bugsnag should capture and report thread-state for all
     * running threads.
     *
     * @param sendThreads should we send thread state with error reports
     * @see #setEnabledReleaseStages
     */
    public void setSendThreads(ThreadSendPolicy sendThreads) {
        config.setSendThreads(sendThreads);
    }

    //
    // Notification
    //


    /**
     * Notify Bugsnag of a handled exception.
     *
     * @param throwable the exception to send to Bugsnag
     * @return true unless the error report was ignored
     */
    public boolean notify(Throwable throwable) {
        HandledState handledState = HandledState.newInstance(
                HandledState.SeverityReasonType.REASON_HANDLED_EXCEPTION);
        BugsnagEvent event = new BugsnagEvent(config, throwable, handledState,
                Thread.currentThread(), featureFlagStore);
        return notify(event);
    }

    /**
     * Notify Bugsnag of a handled exception.
     *
     * @param throwable the exception to send to Bugsnag
     * @param callback  the {@link OnErrorCallback} object to run for this Report
     * @return true unless the error report was ignored
     */
    public boolean notify(Throwable throwable, OnErrorCallback callback) {
        HandledState handledState = HandledState.newInstance(
                HandledState.SeverityReasonType.REASON_HANDLED_EXCEPTION);
        BugsnagEvent event = new BugsnagEvent(config, throwable, handledState,
                Thread.currentThread(), featureFlagStore);
        return notify(event, callback);
    }

    /**
     * Notify Bugsnag of a handled exception - with a severity.
     *
     * @param throwable the exception to send to Bugsnag
     * @param severity  the severity of the error, one of {#link Severity#ERROR},
     *                  {@link Severity#WARNING} or {@link Severity#INFO}
     * @return true unless the error report was ignored
     */
    public boolean notify(Throwable throwable, Severity severity) {
        return notify(throwable, severity, null);
    }

    /**
     * Notify Bugsnag of a handled exception.
     *
     * @param throwable the exception to send to Bugsnag
     * @param severity  the severity of the error, one of {#link Severity#ERROR},
     *                  {@link Severity#WARNING} or {@link Severity#INFO}
     * @param callback  the {@link OnErrorCallback} object to run for this Report
     * @return true unless the error report was ignored
     */
    public boolean notify(Throwable throwable, Severity severity, OnErrorCallback callback) {
        if (throwable == null) {
            LOGGER.warn("Tried to notify with a null Throwable");
            return false;
        }
        if (severity == null) {
            return notify(throwable, callback);
        }

        HandledState handledState = HandledState.newInstance(
                HandledState.SeverityReasonType.REASON_USER_SPECIFIED,
                severity
        );

        BugsnagEvent event = new BugsnagEvent(
                config,
                throwable,
                handledState,
                Thread.currentThread(),
                featureFlagStore
        );
        return notify(event, callback);
    }

    /**
     * Notify Bugsnag of an exception and provide custom diagnostic data
     * for this particular error report.
     *
     * @param event the {@link BugsnagEvent} object to send to Bugsnag
     * @return true unless the error report was ignored
     * @see BugsnagEvent
     */
    public boolean notify(BugsnagEvent event) {
        return notify(event, null);
    }

    boolean notify(Throwable throwable, HandledState handledState, Thread currentThread) {
        BugsnagEvent event = new BugsnagEvent(config, throwable, handledState, currentThread, featureFlagStore);
        return notify(event, null);
    }

    /**
     * Notify Bugsnag of an exception and provide custom diagnostic data
     * for this particular error report.
     *
     * @param event         the {@link BugsnagEvent} object to send to Bugsnag
     * @param reportCallback the {@link OnErrorCallback} object to run for this Report
     * @return false if the error report was ignored
     * @see BugsnagEvent
     */
    public boolean notify(BugsnagEvent event, OnErrorCallback reportCallback) {
        if (event == null) {
            LOGGER.warn("Tried to call notify with a null Report");
            return false;
        }

        // Don't notify if this error class should be ignored
        if (config.shouldIgnoreClass(event.getExceptionName())) {
            LOGGER.debug("Error not reported to Bugsnag - {} is in 'discardClasses'",
                    event.getExceptionName());
            return false;
        }

        // Don't notify unless releaseStage is in enabledReleaseStages
        if (!config.shouldNotifyForReleaseStage()) {
            LOGGER.debug("Error not reported to Bugsnag - {} is not in 'enabledReleaseStages'",
                    config.getReleaseStage());
            return false;
        }

        // Run all client-wide onError callbacks
        for (OnErrorCallback callback : config.callbacks) {
            try {
                boolean proceed = callback.onError(event);
                if (!proceed) {
                    LOGGER.debug("Error not reported to Bugsnag - cancelled by a client-wide onError callback");
                    return false;
                }
            } catch (Throwable ex) {
                LOGGER.warn("Callback threw an exception", ex);
            }
        }

        // Add thread metadata to the report
        event.mergeMetadata(THREAD_METADATA.get());

        // Run the report-specific onError callback, if given
        if (reportCallback != null) {
            try {
                boolean proceed = reportCallback.onError(event);
                if (!proceed) {
                    LOGGER.debug("Error not reported to Bugsnag - cancelled by a report-specific callback");
                    return false;
                }
            } catch (Throwable ex) {
                LOGGER.warn("Callback threw an exception", ex);
            }
        }

        Delivery delivery = config.getDelivery();
        if (delivery == null) {
            LOGGER.debug("Error not reported to Bugsnag - no delivery is set");
            return false;
        }

        // increment session handled/unhandled count
        Session session = sessionTracker.getSession();

        if (session != null) {
            if (event.getUnhandled()) {
                session.incrementUnhandledCount();
            } else {
                session.incrementHandledCount();
            }
            event.setSession(session);
        }

        // Build the notification
        Notification notification = new Notification(config, event);

        // Deliver the notification
        LOGGER.debug("Reporting error to Bugsnag");

        delivery.deliver(config.getSerializer(), notification, config.getErrorApiHeaders());

        return true;
    }

    /**
     * Manually starts tracking a new session.
     * <p>
     * Note: sessions are currently tracked on a per-thread basis. Therefore, if this method were
     * called from Thread A then Thread B, two sessions would be considered 'active'. Any custom
     * strategy used to track sessions should take this into account.
     * <p>
     * Automatic session tracking can be enabled via
     * {@link Bugsnag#setAutoCaptureSessions(boolean)}, which will automatically create a new
     * session for each request
     */
    public void startSession() {
        sessionTracker.startSession(new Date(), false);
    }

    /**
     * Sets whether or not Bugsnag should automatically capture and report User
     * sessions for each request.
     * <p>
     * By default this behavior is disabled.
     *
     * @param autoCaptureSessions whether sessions should be captured automatically
     */
    public void setAutoCaptureSessions(boolean autoCaptureSessions) {
        config.setAutoCaptureSessions(autoCaptureSessions);
    }

    /**
     * Retrieves whether or not Bugsnag should automatically capture
     * and report User sessions for each request.
     *
     * @return whether sessions should be auto captured
     */
    public boolean shouldAutoCaptureSessions() {
        return config.shouldAutoCaptureSessions();
    }

    /**
     * Set the endpoint to deliver Bugsnag sessions to. This is a convenient
     * shorthand for bugsnag.getSessionDelivery().setEndpoint();
     *
     * @param endpoint the endpoint to send sessions to
     * @see #setDelivery
     * @deprecated use {@link Configuration#setEndpoints(String, String)} instead
     */
    @Deprecated
    public void setSessionEndpoint(String endpoint) {
        Delivery sessionDelivery = config.getSessionDelivery();
        if (sessionDelivery instanceof HttpDelivery) {
            ((HttpDelivery) sessionDelivery).setEndpoint(endpoint);
        }
    }

    /**
     * @deprecated Use {@link #setEndpoints(EndpointConfiguration)} instead.
     */
    @Deprecated
    public void setEndpoints(String notify, String sessions) throws IllegalArgumentException {
        setEndpoints(new EndpointConfiguration(notify, sessions));
    }

    /**
     * Set the endpoints to send data to. Use this to override the default endpoints
     * if you are using Bugsnag Enterprise to point to your own Bugsnag endpoint.
     * <p>
     * Please note that it is recommended that you set both endpoints. If the notify endpoint is
     * missing, an exception will be thrown. If the session endpoint is missing, a warning will be
     * logged and sessions will not be sent automatically.
     * <p>
     * Note that if you are setting a custom {@link Delivery}, this method should be called after
     * the custom implementation has been set.
     *
     * @param endpointConfiguration the endpoint configuration
     * @throws IllegalArgumentException if the endpoint configuration is null or if the notify endpoint is empty or null
     */
    public void setEndpoints(EndpointConfiguration endpointConfiguration) throws IllegalArgumentException {
        config.setEndpoints(endpointConfiguration);
    }

    /**
     * Close the connection to Bugsnag and unlink the exception handler.
     */
    @Override
    public void close() {
        LOGGER.debug("Closing connection to Bugsnag");
        ExceptionHandler.disable(this);

        // runs periodic checks, should shut down immediately as don't need to send any
        // sessions
        sessionExecutorService.shutdownNow();

        // flush remaining sessions
        sessionTracker.shutdown();

        Delivery delivery = config.getDelivery();
        if (delivery != null) {
            delivery.close();
        }
    }

    // Thread metadata

    /**
     * Add a key value pair to a metadata tab just for this thread.
     *
     * @param tabName the name of the tab to add the key value pair to
     * @param key     the key of the metadata to add
     * @param value   the metadata value to add
     */
    public static void addThreadMetadata(String tabName, String key, Object value) {
        THREAD_METADATA.get().addMetadata(tabName, key, value);
    }

    /**
     * Clears all metadata added to the current thread
     */
    public static void clearThreadMetadata() {
        THREAD_METADATA.get().clear();
    }

    /**
     * Clears all metadata added to a given tab on the current thread
     *
     * @param tabName the name of the tab to remove
     */
    public static void clearThreadMetadata(String tabName) {
        THREAD_METADATA.get().clearMetadata(tabName);
    }

    /**
     * Clears a metadata key/value pair from a tab on the current thread
     *
     * @param tabName the name of the tab to that the metadata is in
     * @param key     the key of the metadata to remove
     */
    public static void clearThreadMetadata(String tabName, String key) {
        THREAD_METADATA.get().clearMetadata(tabName, key);
    }

    Configuration getConfig() {
        return config;
    }

    SessionTracker getSessionTracker() {
        return sessionTracker;
    }

    /**
     * Retrieves all instances of {@link Bugsnag} which are registered to
     * catch uncaught exceptions.
     *
     * @return clients which catch uncaught exceptions
     */
    public static Set<Bugsnag> uncaughtExceptionClients() {
        UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
        if (handler instanceof ExceptionHandler) {
            ExceptionHandler bugsnagHandler = (ExceptionHandler) handler;
            return Collections.unmodifiableSet(bugsnagHandler.uncaughtExceptionClients());
        }
        return Collections.emptySet();
    }

    void addOnSession(OnSession onSession) {
        sessionTracker.addOnSession(onSession);
    }

    /**
     * Add a feature flag with the specified name and variant.
     * If the name already exists, the variant will be updated.
     *
     * @param name the feature flag name
     * @param variant the feature flag variant (can be null)
     */
    public void addFeatureFlag(String name, String variant) {
        featureFlagStore.addFeatureFlag(name, variant);
    }

    /**
     * Add a feature flag with the specified name and no variant.
     *
     * @param name the feature flag name
     */
    public void addFeatureFlag(String name) {
        addFeatureFlag(name, null);
    }

    /**
     * Add multiple feature flags.
     * If any names already exist, their variants will be updated.
     *
     * @param featureFlags the feature flags to add
     */
    public void addFeatureFlags(Collection<FeatureFlag> featureFlags) {
        featureFlagStore.addFeatureFlags(featureFlags);
    }

    /**
     * Remove the feature flag with the specified name.
     *
     * @param name the feature flag name to remove
     */
    public void clearFeatureFlag(String name) {
        featureFlagStore.clearFeatureFlag(name);
    }

    /**
     * Remove all feature flags.
     */
    public void clearFeatureFlags() {
        featureFlagStore.clearFeatureFlags();
    }

    /**
     * Get a copy of the feature flag store.
     *
     * @return a copy of the feature flag store
     */
    FeatureFlagStore copyFeatureFlagStore() {
        return featureFlagStore.copy();
    }

    /**
     * Get a copy of the feature flag store (package-private for testing).
     *
     * @return a copy of the feature flag store
     */
    FeatureFlagStore getFeatureFlagStoreCopy() {
        return featureFlagStore.copy();
    }
}
