package com.bugsnag;

public class TestHooks {

    public static void disableSendUncaughtExceptions(Bugsnag bugsnag) {
        bugsnag.getConfig().setSendUncaughtExceptions(false);
    }
}
