package com.bugsnag;

public interface Plugin {
    void load(Bugsnag bugsnag);

    void unload();
}
