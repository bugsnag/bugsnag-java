package com.bugsnag.callbacks;

import com.bugsnag.BugsnagEvent;

public interface Callback {
    /**
     * Perform changes to the report before delivery.
     * Return {@code true} to continue sending, or {@code false} to cancel delivery.
     * Implementations may also call {@code report.cancel()} for backward
     * compatibility.
     *
     * @param event the report to perform changes on.
     * @return true to send, false to suppress delivery
     */
    boolean onError(BugsnagEvent event);
}
