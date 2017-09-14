package com.bugsnag;

final class EventHandledState {

    enum SeverityReasonType {
        EXCEPTION_HANDLER("exception_handler"),
        MIDDLEWARE_HANDLER("middleware_handler"),
        LOG_LEVEL("log_level"),
        ERROR_CLASS("error_class");

        private final String name;

        SeverityReasonType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final Severity originalSeverity;
    private final boolean unhandled;

    EventHandledState(Severity originalSeverity, SeverityReasonType severityReasonType) {
        this.originalSeverity = originalSeverity;
        this.unhandled = severityReasonType != null;
    }

    boolean isDefaultSeverity(Severity currentSeverity) {
        return originalSeverity == currentSeverity;
    }

    boolean isUnhandled() {
        return unhandled;
    }

}
