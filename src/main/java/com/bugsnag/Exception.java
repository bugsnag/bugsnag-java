package com.bugsnag;

import com.bugsnag.serialization.Expose;

import java.util.List;

class Exception {
    private Configuration config;
    private Throwable throwable;

    Exception(Configuration config, Throwable throwable) {
        this.config = config;
        this.throwable = throwable;
    }

    @Expose
    public String getErrorClass() {
        return throwable.getClass().getName();
    }

    @Expose
    public String getMessage() {
        return throwable.getLocalizedMessage();
    }

    @Expose
    public List<Stackframe> getStacktrace() {
        return Stackframe.getStacktrace(config, throwable.getStackTrace());
    }
}
