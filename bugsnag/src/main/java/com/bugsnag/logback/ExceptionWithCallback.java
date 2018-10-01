package com.bugsnag.logback;

import com.bugsnag.callbacks.Callback;

/**
 * Used to send errors to the logger with a Bugsnag callback
 */
public class ExceptionWithCallback extends Throwable {

    private static final long serialVersionUID = -7437662816078385957L;
    private Callback callback;

    /**
     * Creates an error with a Bugsnag callback
     * Used to associate meta data with error reports sent via a logger
     *
     * @param cause The original error
     * @param callback The Bugsnag callback
     */
    public ExceptionWithCallback(Throwable cause, Callback callback) {
        // Use the same message as the cause, so the same message appears in other logs
        super(cause.getMessage(), cause);

        this.callback = callback;

        // Set the stack traces to the same as the cause, so it appears in other logs
        this.setStackTrace(cause.getStackTrace());
    }

    /**
     * @return Bugsnag callback associated with this error
     */
    public Callback getCallback() {
        return callback;
    }

    /**
     * @param callback Bugsnag callback associated with this error
     */
    public void setCallback(Callback callback) {
        this.callback = callback;
    }
}
