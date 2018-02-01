package com.bugsnag;

import com.bugsnag.callbacks.Callback;
import com.bugsnag.delivery.Delivery;
import com.bugsnag.delivery.HttpDelivery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Proxy;
import java.util.Date;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Bugsnag {
    private static final Logger LOGGER = LoggerFactory.getLogger(Bugsnag.class);
    private static final int SHUTDOWN_TIMEOUT = 5000;
    private static final int SESSION_TRACKING_PERIOD_SECS = 60;

    private ScheduledThreadPoolExecutor sessionExecutorService =
            new ScheduledThreadPoolExecutor(1, new RejectedExecutionHandler() {
                @Override
                public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
                    LOGGER.error("Rejected execution for sessionExecutorService");
                }
            });

    private Configuration config;
    private final SessionTracker sessionTracker;

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
        ServletSessionTracker.INSTANCE.setSessionTracker(sessionTracker);

        // Automatically send unhandled exceptions to Bugsnag using this Bugsnag
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
                sessionTracker.flushSessions(new Date());
            }
        }, SESSION_TRACKING_PERIOD_SECS, SESSION_TRACKING_PERIOD_SECS, TimeUnit.SECONDS);
    }

    private void addSessionTrackingShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                sessionTracker.setShuttingDown(true);
                sessionExecutorService.shutdown();
                try {
                    if (!sessionExecutorService
                            .awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.MILLISECONDS)) {
                        LOGGER.warn("Shutdown of 'session tracking' threads"
                                + " took too long - forcing a shutdown");
                        sessionExecutorService.shutdownNow();
                    }
                } catch (InterruptedException ex) {
                    LOGGER.warn("Shutdown of 'session tracking' thread "
                            + "was interrupted - forcing a shutdown");
                    sessionExecutorService.shutdownNow();
                }
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
     * @see Callback
     */
    public void addCallback(Callback callback) {
        config.addCallback(callback);
    }

    /**
     * Get the delivery to use to send reports.
     *
     * @return the delivery to use to send reports.
     * @see Delivery
     */
    public Delivery getDelivery() {
        return config.delivery;
    }

    /**
     * Set the application type sent to Bugsnag.
     *
     * @param appType the app type to send, eg. spring, gradleTask
     */
    public void setAppType(String appType) {
        config.appType = appType;
    }

    /**
     * Set the application version sent to Bugsnag.
     *
     * @param appVersion the app version to send
     */
    public void setAppVersion(String appVersion) {
        config.appVersion = appVersion;
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
        config.delivery = delivery;
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
        config.sessionDelivery = delivery;
    }

    /**
     * Set the endpoint to deliver Bugsnag errors report to. This is a convenient
     * shorthand for bugsnag.getDelivery().setEndpoint();
     *
     * @param endpoint the endpoint to send reports to
     * @see #setDelivery
     */
    public void setEndpoint(String endpoint) {
        if (config.delivery instanceof HttpDelivery) {
            ((HttpDelivery) config.delivery).setEndpoint(endpoint);
        }
    }

    /**
     * Set which keys should be filtered when sending metaData to Bugsnag.
     * Use this when you want to ensure sensitive information, such as passwords
     * or credit card information is stripped from metaData you send to Bugsnag.
     * Any keys in metaData which contain these strings will be marked as
     * [FILTERED] when send to Bugsnag.
     *
     * @param filters a list of String keys to filter from metaData
     */
    public void setFilters(String... filters) {
        config.filters = filters;
    }

    /**
     * Set which exception classes should be ignored (not sent) by Bugsnag.
     *
     * @param ignoreClasses a list of exception classes to ignore
     */
    public void setIgnoreClasses(String... ignoreClasses) {
        config.ignoreClasses = ignoreClasses;
    }

    /**
     * Set for which releaseStages errors should be sent to Bugsnag.
     * Use this to stop errors from development builds being sent.
     *
     * @param notifyReleaseStages a list of releaseStages to notify for
     * @see #setReleaseStage
     */
    public void setNotifyReleaseStages(String... notifyReleaseStages) {
        config.notifyReleaseStages = notifyReleaseStages;
    }

    /**
     * Set which packages should be considered part of your application.
     * Bugsnag uses this to help with error grouping, and stacktrace display.
     *
     * @param projectPackages a list of package names
     */
    public void setProjectPackages(String... projectPackages) {
        config.projectPackages = projectPackages;
    }

    /**
     * Set a proxy to use when delivering Bugsnag error reports and sessions. This is a convenient
     * shorthand for bugsnag.getDelivery().setProxy();
     *
     * @param proxy the proxy to use to send reports
     */
    public void setProxy(Proxy proxy) {
        if (config.delivery instanceof HttpDelivery) {
            ((HttpDelivery) config.delivery).setProxy(proxy);
        }
        if (config.sessionDelivery instanceof HttpDelivery) {
            ((HttpDelivery) config.sessionDelivery).setProxy(proxy);
        }
    }

    /**
     * Set the current "release stage" of your application.
     *
     * @param releaseStage the release stage of the app
     * @see #setNotifyReleaseStages
     */
    public void setReleaseStage(String releaseStage) {
        config.releaseStage = releaseStage;
    }

    /**
     * Set whether Bugsnag should capture and report thread-state for all
     * running threads. This is often not useful for Java web apps, since
     * there could be thousands of active threads depending on your
     * environment.
     *
     * @param sendThreads should we send thread state with error reports
     * @see #setNotifyReleaseStages
     */
    public void setSendThreads(boolean sendThreads) {
        config.sendThreads = sendThreads;
    }

    /**
     * Set a timeout (in ms) to use when delivering Bugsnag error reports and sessions.
     * This is a convenient shorthand for bugsnag.getDelivery().setTimeout();
     *
     * @param timeout the timeout to set (in ms)
     * @see #setDelivery
     */
    public void setTimeout(int timeout) {
        if (config.delivery instanceof HttpDelivery) {
            ((HttpDelivery) config.delivery).setTimeout(timeout);
        }
        if (config.sessionDelivery instanceof HttpDelivery) {
            ((HttpDelivery) config.sessionDelivery).setTimeout(timeout);
        }
    }


    //
    // Notification
    //

    /**
     * Build an Report object to send to Bugsnag.
     *
     * @param throwable the exception to send to Bugsnag
     * @return the report object
     * @see Report
     * @see #notify(com.bugsnag.Report)
     */
    public Report buildReport(Throwable throwable) {
        return new Report(config, throwable);
    }

    /**
     * Notify Bugsnag of a handled exception.
     *
     * @param throwable the exception to send to Bugsnag
     * @return true unless the error report was ignored
     */
    public boolean notify(Throwable throwable) {
        return notify(buildReport(throwable));
    }

    /**
     * Notify Bugsnag of a handled exception.
     *
     * @param throwable the exception to send to Bugsnag
     * @param callback  the {@link Callback} object to run for this Report
     * @return true unless the error report was ignored
     */
    public boolean notify(Throwable throwable, Callback callback) {
        return notify(buildReport(throwable), callback);
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
     * @param callback  the {@link Callback} object to run for this Report
     * @return true unless the error report was ignored
     */
    public boolean notify(Throwable throwable, Severity severity, Callback callback) {
        if (throwable == null) {
            LOGGER.warn("Tried to notify with a null Throwable");
            return false;
        }

        HandledState handledState = HandledState.newInstance(
                HandledState.SeverityReasonType.REASON_USER_SPECIFIED, severity);
        Report report = new Report(config, throwable, handledState);
        return notify(report, callback);
    }

    /**
     * Notify Bugsnag of an exception and provide custom diagnostic data
     * for this particular error report.
     *
     * @param report the {@link Report} object to send to Bugsnag
     * @return true unless the error report was ignored
     * @see Report
     * @see #buildReport
     */
    public boolean notify(Report report) {
        return notify(report, null);
    }


    boolean notify(Throwable throwable, HandledState handledState) {
        Report report = new Report(config, throwable, handledState);
        return notify(report, null);
    }

    /**
     * Notify Bugsnag of an exception and provide custom diagnostic data
     * for this particular error report.
     *
     * @param report         the {@link Report} object to send to Bugsnag
     * @param reportCallback the {@link Callback} object to run for this Report
     * @return false if the error report was ignored
     * @see Report
     * @see #buildReport
     */
    public boolean notify(Report report, Callback reportCallback) {
        if (report == null) {
            LOGGER.warn("Tried to call notify with a null Report");
            return false;
        }

        // Don't notify if this error class should be ignored
        if (config.shouldIgnoreClass(report.getExceptionName())) {
            LOGGER.debug("Error not reported to Bugsnag - {} is in 'ignoreClasses'",
                report.getExceptionName());
            return false;
        }

        // Don't notify unless releaseStage is in notifyReleaseStages
        if (!config.shouldNotifyForReleaseStage()) {
            LOGGER.debug("Error not reported to Bugsnag - {} is not in 'notifyReleaseStages'",
                config.releaseStage);
            return false;
        }

        // Run all client-wide beforeNotify callbacks
        for (Callback callback : config.callbacks) {
            try {
                // Run the callback
                callback.beforeNotify(report);

                // Check if callback cancelled delivery
                if (report.getShouldCancel()) {
                    LOGGER.debug("Error not reported to Bugsnag - "
                        + "cancelled by a client-wide beforeNotify callback");
                    return false;
                }
            } catch (Throwable ex) {
                LOGGER.warn("Callback threw an exception", ex);
            }
        }

        // Run the report-specific beforeNotify callback, if given
        if (reportCallback != null) {
            try {
                // Run the callback
                reportCallback.beforeNotify(report);

                // Check if callback cancelled delivery
                if (report.getShouldCancel()) {
                    LOGGER.debug(
                        "Error not reported to Bugsnag - cancelled by a report-specific callback");
                    return false;
                }
            } catch (Throwable ex) {
                LOGGER.warn("Callback threw an exception", ex);
            }
        }

        if (config.delivery == null) {
            LOGGER.debug("Error not reported to Bugsnag - no delivery is set");
            return false;
        }

        // increment session handled/unhandled count
        Session session = sessionTracker.getSession();

        if (session != null) {
            if (report.getUnhandled()) {
                session.incrementUnhandledCount();
            } else {
                session.incrementHandledCount();
            }
            report.setSession(session);
        }

        // Build the notification
        Notification notification = new Notification(config, report);

        // Deliver the notification
        LOGGER.debug("Reporting error to Bugsnag");

        config.delivery.deliver(config.serializer, notification, config.getErrorApiHeaders());

        return true;
    }

    /**
     * Manually starts tracking a new session.
     *
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
     * Set the endpoint to deliver Bugsnag sessions to. This is a convenient
     * shorthand for bugsnag.getSessionDelivery().setEndpoint();
     *
     * @param endpoint the endpoint to send reports to
     * @see #setDelivery
     */
    public void setSessionEndpoint(String endpoint) {
        if (config.sessionDelivery instanceof HttpDelivery) {
            ((HttpDelivery) config.sessionDelivery).setEndpoint(endpoint);
        }
    }

    /**
     * Close the connection to Bugsnag and unlink the exception handler.
     */
    public void close() {
        LOGGER.debug("Closing connection to Bugsnag");
        config.delivery.close();
        ExceptionHandler.disable(this);
    }
}
