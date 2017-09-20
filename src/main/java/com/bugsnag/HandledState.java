package com.bugsnag;

final class HandledState {

    enum SeverityReasonType {
        REASON_UNHANDLED_EXCEPTION("unhandledException"),
        REASON_HANDLED_EXCEPTION("handledException"),
        REASON_USER_SPECIFIED("userSpecifiedSeverity"),
        REASON_CALLBACK_SPECIFIED("userCallbackSetSeverity");

        private final String name;

        SeverityReasonType(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final SeverityReasonType severityReasonType;
    private final Severity originalSeverity;
    private final boolean unhandled;

    private Severity currentSeverity;

    static HandledState newInstance(SeverityReasonType severityReasonType) {
        return newInstance(severityReasonType, null);
    }

    static HandledState newInstance(SeverityReasonType severityReasonType, Severity severity) {
        switch (severityReasonType) {
            case REASON_UNHANDLED_EXCEPTION:
                return new HandledState(severityReasonType, Severity.ERROR, true, null);
            case REASON_HANDLED_EXCEPTION:
                return new HandledState(severityReasonType, Severity.WARNING, false, null);
            case REASON_USER_SPECIFIED:
                return new HandledState(severityReasonType, severity, false, null);
            default:
                throw new IllegalArgumentException("Invalid arg for reason: " + severityReasonType);
        }
    }

    private HandledState(SeverityReasonType severityReasonType, Severity currentSeverity, boolean unhandled,
                         String attributeValue) {
        this.severityReasonType = severityReasonType;
        this.originalSeverity = currentSeverity;
        this.unhandled = unhandled;
        this.currentSeverity = currentSeverity;
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

    Severity getOriginalSeverity() {
        return originalSeverity;
    }

    SeverityReasonType calculateSeverityReasonType() {
        return originalSeverity == currentSeverity ?
                severityReasonType : SeverityReasonType.REASON_CALLBACK_SPECIFIED;
    }

    Severity getCurrentSeverity() {
        return currentSeverity;
    }

    void setCurrentSeverity(Severity severity) {
        this.currentSeverity = severity;
    }

}
