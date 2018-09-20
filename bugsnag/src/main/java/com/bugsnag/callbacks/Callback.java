package com.bugsnag.callbacks;

import com.bugsnag.Report;

public interface Callback {
    /**
     * Perform changes to the report before delivery.
     *
     * @param report the report to perform changes on.
     */
    void beforeNotify(Report report);
}
