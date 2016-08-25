package com.bugsnag.callbacks;

import com.bugsnag.Report;

public interface Callback {
    public abstract void beforeNotify(Report report);
}
