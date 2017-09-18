package com.bugsnag;

final class HandledState {

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
    private final SeverityReasonType severityReasonType;
    private final String description;

    HandledState(Severity originalSeverity,
                 SeverityReasonType severityReasonType,
                 String description) {
        this.originalSeverity = originalSeverity;
        this.unhandled = severityReasonType != null;
        this.severityReasonType = severityReasonType;
        this.description = description;
    }

    boolean isDefaultSeverity(Severity currentSeverity) {
        return originalSeverity == currentSeverity;
    }

    boolean isUnhandled() {
        return unhandled;
    }

    SeverityReasonType getSeverityReasonType() {
        return severityReasonType;
    }

    String getDescription() {
        return description;
    }

    Severity getOriginalSeverity() {
        return originalSeverity;
    }
}
