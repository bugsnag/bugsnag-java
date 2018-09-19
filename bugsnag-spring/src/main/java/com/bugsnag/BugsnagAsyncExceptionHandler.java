package com.bugsnag;

import com.bugsnag.HandledState.SeverityReasonType;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;

import java.lang.reflect.Method;
import java.util.Collections;

/**
 * Reports uncaught exceptions thrown from async methods to Bugsnag.
 *
 * This must be enabled in applications manually by extending {@link AsyncConfigurerSupport}
 * or implementing {@link AsyncConfigurer}.
 */
public class BugsnagAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    private final Bugsnag bugsnag;

    public BugsnagAsyncExceptionHandler(final Bugsnag bugsnag) {
        this.bugsnag = bugsnag;
    }

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
        if (bugsnag.getConfig().shouldSendUncaughtExceptions()) {
            HandledState handledState = HandledState.newInstance(
                    SeverityReasonType.REASON_UNHANDLED_EXCEPTION_MIDDLEWARE,
                    Collections.singletonMap("framework", "Spring"),
                    Severity.ERROR,
                    true);

            bugsnag.notify(throwable, handledState);
        }
    }
}
