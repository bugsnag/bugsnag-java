package com.bugsnag;

import java.lang.module.Configuration;

public interface Plugin {
    public void load();
    public void unload();
}