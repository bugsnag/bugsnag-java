package com.bugsnag;

import com.bugsnag.serialization.Expose;

import java.util.List;

public class BugsnagError {
    private Configuration config;
    private Throwable throwable;
    private String errorClass;

    BugsnagError(Configuration config, Throwable throwable) {
        this.config = config;
        this.throwable = throwable;
        this.errorClass = throwable.getClass().getName();
    }

    @Expose
    public String getErrorClass() {
        return errorClass;
    }

    @Expose
    public String getMessage() {
        return throwable.getLocalizedMessage();
    }

    @Expose
    public List<Stackframe> getStacktrace() {
        return Stackframe.getStacktrace(config, throwable.getStackTrace());
    }

    public void setErrorClass(String errorClass) {
        this.errorClass = errorClass;
    }

    public Throwable getThrowable() {
        return throwable;
    }
}
