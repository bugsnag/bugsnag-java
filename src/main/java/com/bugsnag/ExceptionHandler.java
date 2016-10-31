package com.bugsnag;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.WeakHashMap;

class ExceptionHandler implements UncaughtExceptionHandler {
    private final UncaughtExceptionHandler originalHandler;
    private final WeakHashMap<Bugsnag, Boolean> clientMap = new WeakHashMap<Bugsnag, Boolean>();

    static void enable(Bugsnag bugsnag) {
        UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();

        // Find or create the Bugsnag ExceptionHandler
        ExceptionHandler bugsnagHandler;
        if (currentHandler instanceof ExceptionHandler) {
            bugsnagHandler = (ExceptionHandler) currentHandler;
        } else {
            bugsnagHandler = new ExceptionHandler(currentHandler);
            Thread.setDefaultUncaughtExceptionHandler(bugsnagHandler);
        }

        // Subscribe this bugsnag to uncaught exceptions
        bugsnagHandler.clientMap.put(bugsnag, true);
    }

    static void disable(Bugsnag bugsnag) {
        // Find the Bugsnag ExceptionHandler
        UncaughtExceptionHandler currentHandler = Thread.getDefaultUncaughtExceptionHandler();
        if (currentHandler instanceof ExceptionHandler) {
            // Unsubscribe this bugsnag from uncaught exceptions
            ExceptionHandler bugsnagHandler = (ExceptionHandler) currentHandler;
            bugsnagHandler.clientMap.remove(bugsnag);

            // Remove the Bugsnag ExceptionHandler if no clients are subscribed
            if (bugsnagHandler.clientMap.size() == 0) {
                Thread.setDefaultUncaughtExceptionHandler(bugsnagHandler.originalHandler);
            }
        }
    }

    ExceptionHandler(UncaughtExceptionHandler originalHandler) {
        this.originalHandler = originalHandler;
    }

    public void uncaughtException(Thread thread, Throwable throwable) {
        // Notify any subscribed clients of the uncaught exception
        for (Bugsnag bugsnag : clientMap.keySet()) {
            bugsnag.notify(throwable, Severity.ERROR);
        }

        // Pass exception on to original exception handler
        if (originalHandler != null) {
            originalHandler.uncaughtException(thread, throwable);
        } else {
            // Emulate the java exception print style
            System.err.printf("Exception in thread \"%s\" ", thread.getName());
            throwable.printStackTrace(System.err);
        }
    }
}
