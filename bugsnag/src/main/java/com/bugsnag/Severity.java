package com.bugsnag;

public enum Severity {
    ERROR("error"),
    WARNING("warning"),
    INFO("info");

    private final String value;

    Severity(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
