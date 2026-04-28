package com.bugsnag;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;


class ExceptionHandler implements UncaughtExceptionHandler {
    private static volatile ExceptionHandler singletonInstance = null;

    private final UncaughtExceptionHandler originalHandler;
    private final AtomicReference<WeakReference<Bugsnag>[]> weakClients = new AtomicReference<>(null);
    private final AtomicBoolean installed = new AtomicBoolean(false);

    /**
     * Returns all the Bugsnag instances associated with this ExceptionHandler.
     *
     * @return an immutable iterable of the Bugsnag instances associated with this ExceptionHandler (never null)
     */
    Iterable<Bugsnag> uncaughtExceptionClients() {
        WeakReference<Bugsnag>[] clientRefs = weakClients.get();
        if (clientRefs == null || clientRefs.length == 0) {
            return Collections.emptySet();
        }

        return () -> Stream.of(clientRefs)
                .map(WeakReference::get)
                .filter(Objects::nonNull)
                .iterator();
    }

    static void enable(Bugsnag bugsnag) {
        ExceptionHandler handler = getGlobalOrCreate();
        handler.add(bugsnag);

        if (handler.installed.compareAndSet(false, true)) {
            Thread.setDefaultUncaughtExceptionHandler(handler);
        }
    }

    static void disable(Bugsnag bugsnag) {
        ExceptionHandler handler = getGlobalOrNull();
        if (handler == null) {
            return; // No handler installed, so nothing to disable
        }

        if (handler.remove(bugsnag)) {
            handler.cleanup();
        }
    }

    ExceptionHandler(UncaughtExceptionHandler originalHandler) {
        this.originalHandler = originalHandler;
    }

    @Override
    public void uncaughtException(java.lang.Thread thread, Throwable throwable) {
        // Notify any subscribed clients of the uncaught exception
        for (Bugsnag bugsnag : uncaughtExceptionClients()) {
            if (bugsnag.getConfig().getAutoDetectErrors()) {
                HandledState handledState = HandledState.newInstance(
                        HandledState.SeverityReasonType.REASON_UNHANDLED_EXCEPTION,
                        Severity.ERROR
                );

                bugsnag.notify(throwable, handledState, thread);
            }
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

    @SuppressWarnings("unchecked")
    void add(Bugsnag bugsnag) {
        WeakReference<Bugsnag> newRef = new WeakReference<>(bugsnag);

        while (true) {
            final WeakReference<Bugsnag>[] currentRefs = weakClients.get();

            if (currentRefs != null) {
                // Create a new array with the new client added
                WeakReference<Bugsnag>[] newRefs = new WeakReference[currentRefs.length + 1];
                int index = 0;
                for (WeakReference<Bugsnag> ref : currentRefs) {
                    // Copy existing non-garbage collected references
                    // It's not as fast as System.arraycopy, but it avoids needing even more copies of the array
                    if (ref.get() != null) {
                        newRefs[index++] = ref;
                    }
                }
                newRefs[index++] = newRef;

                if (index < newRefs.length) {
                    // If some references were garbage collected, create a smaller array
                    newRefs = Arrays.copyOf(newRefs, index);
                }

                // Attempt to update the reference atomically
                if (weakClients.compareAndSet(currentRefs, newRefs)) {
                    return; // Successfully added the client
                }
            } else {
                WeakReference<Bugsnag>[] newRefs = new WeakReference[] {
                    newRef
                };

                // Attempt to update the reference atomically
                if (weakClients.compareAndSet(null, newRefs)) {
                    return; // Successfully added the client
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    boolean remove(Bugsnag bugsnag) {
        while (true) {
            final WeakReference<Bugsnag>[] currentRefs = weakClients.get();
            if (currentRefs == null || currentRefs.length == 0) {
                return false;
            }

            // Create a new array with the specified client removed
            WeakReference<Bugsnag>[] newRefs = new WeakReference[currentRefs.length - 1];
            int index = 0;
            for (WeakReference<Bugsnag> ref : currentRefs) {
                Bugsnag client = ref.get();
                if (client != null && client != bugsnag) {
                    if (index >= newRefs.length) {
                        // if we reached this point, then 'bugsnag' wasn't in the list - this will always be
                        // the last element in the list, and it wasn't the one we are trying to remove so we
                        // can exit here
                        return false;
                    }
                    newRefs[index++] = ref;
                }
            }

            if (index < newRefs.length) {
                // If some references were garbage collected, create a smaller array
                newRefs = Arrays.copyOf(newRefs, index);
            }

            if (weakClients.compareAndSet(currentRefs, newRefs)) {
                // Return true if the array changed (target removed or GC'd refs cleaned)
                return index < currentRefs.length;
            }
        }
    }

    private void cleanup() {
        WeakReference<Bugsnag>[] refs = weakClients.get();
        if (refs != null && refs.length > 0) {
            // this instance still has clients
            return;
        }

        if (isCurrentInstalledHandler() && installed.compareAndSet(true, false)) {
            Thread.setDefaultUncaughtExceptionHandler(originalHandler);
        }
    }

    /**
     * Return true if this instance is currently the default uncaught exception handler. If this instance is installed
     * as a handler, but is not the outermost/current handler this will return false (meaning it is not safe to
     * uninstall this handler as it is possibly being delegated to by another crash handler).
     *
     * @return true if this instance is currently the default uncaught exception handler, false otherwise
     */
    private boolean isCurrentInstalledHandler() {
        return Thread.getDefaultUncaughtExceptionHandler() == this;
    }

    static ExceptionHandler getGlobalOrCreate() {
        if (singletonInstance == null) {
            synchronized (ExceptionHandler.class) {
                if (singletonInstance == null) {
                    singletonInstance = new ExceptionHandler(Thread.getDefaultUncaughtExceptionHandler());
                }
            }
        }
        return singletonInstance;
    }

    static ExceptionHandler getGlobalOrNull() {
        return singletonInstance;
    }
}
