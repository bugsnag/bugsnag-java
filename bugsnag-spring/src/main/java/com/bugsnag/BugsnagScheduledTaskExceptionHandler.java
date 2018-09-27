package com.bugsnag;

import com.bugsnag.HandledState.SeverityReasonType;

import org.springframework.util.ErrorHandler;

import java.util.Collections;

/**
 * Reports uncaught exceptions thrown from scheduled task execution to Bugsnag
 * and then passes the exception to any other existing error handler.
 */
class BugsnagScheduledTaskExceptionHandler implements ErrorHandler {

    private final Bugsnag bugsnag;

    private ErrorHandler existingErrorHandler;

    BugsnagScheduledTaskExceptionHandler(Bugsnag bugsnag) {
        this.bugsnag = bugsnag;
    }

    @Override
    public void handleError(Throwable throwable) {
        if (bugsnag.getConfig().shouldSendUncaughtExceptions()) {
            HandledState handledState = HandledState.newInstance(
                    SeverityReasonType.REASON_UNHANDLED_EXCEPTION_MIDDLEWARE,
                    Collections.singletonMap("framework", "Spring"),
                    Severity.ERROR,
                    true);

            bugsnag.notify(throwable, handledState, Thread.currentThread());
        }

        if (existingErrorHandler != null
                && !(existingErrorHandler instanceof BugsnagScheduledTaskExceptionHandler)) {
            existingErrorHandler.handleError(throwable);
        }
    }

    void setExistingErrorHandler(final ErrorHandler existingErrorHandler) {
        this.existingErrorHandler = existingErrorHandler;
    }
}
