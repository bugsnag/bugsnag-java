package com.bugsnag;

final class EventHandledState {

    private final Severity originalSeverity;
    private final boolean unhandled;

    EventHandledState(Severity originalSeverity, boolean unhandled) {
        this.originalSeverity = originalSeverity;
        this.unhandled = unhandled;
    }

    boolean isDefaultSeverity(Severity currentSeverity) {
        return originalSeverity == currentSeverity;
    }

    boolean isUnhandled() {
        return unhandled;
    }

}
