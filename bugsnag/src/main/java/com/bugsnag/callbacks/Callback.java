package com.bugsnag.callbacks;

import com.bugsnag.Report;

public interface Callback {
    /**
     * Perform changes to the event before delivery.
     *
     * @param report the report to perform changes on.
     */
    void onError(Report report);

    /**
     * Perform changes to the report before delivery.
     *
     * @param report the report to perform changes on.
     * @deprecated use {@link #onError(Report)} instead
     */
    @Deprecated
    default void beforeNotify(Report report) {
        onError(report);
    }
}
