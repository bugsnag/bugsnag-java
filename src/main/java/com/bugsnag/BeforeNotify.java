package com.bugsnag;

public interface BeforeNotify {
    abstract boolean run (Error error);
}
