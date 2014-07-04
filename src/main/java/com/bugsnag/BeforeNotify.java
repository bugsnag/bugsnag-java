package com.bugsnag;

public interface BeforeNotify {
    abstract void run (Error error);
}
