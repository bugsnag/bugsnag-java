package com.bugsnag;

import com.bugsnag.serialization.Expose;

import java.util.ArrayList;
import java.util.List;

class Stackframe {
    private Configuration config;
    private StackTraceElement el;

    Stackframe(Configuration config, StackTraceElement el) {
        this.config = config;
        this.el = el;
    }

    static List<Stackframe> getStacktrace(Configuration config, StackTraceElement[] elements) {
        List<Stackframe> stacktrace = new ArrayList<Stackframe>();
        for (StackTraceElement el : elements) {
            stacktrace.add(new Stackframe(config, el));
        }

        return stacktrace;
    }

    @Expose
    public String getFile() {
        return el.getFileName() == null ? "Unknown" : el.getFileName();
    }

    @Expose
    public String getMethod() {
        return el.getClassName() + "." + el.getMethodName();
    }

    @Expose
    public int getLineNumber() {
        return el.getLineNumber();
    }

    @Expose
    public boolean isInProject() {
        return config.inProject(el.getClassName());
    }
}
