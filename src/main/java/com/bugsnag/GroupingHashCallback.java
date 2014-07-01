package com.bugsnag;

public interface GroupingHashCallback {
    abstract String run(Throwable error);
}
