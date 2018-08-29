package com.bugsnag;

import java.util.Collections;
import java.util.Map;

final class HandledState {

    enum SeverityReasonType {
        REASON_UNHANDLED_EXCEPTION("unhandledException"),
        REASON_HANDLED_EXCEPTION("handledException"),
        REASON_USER_SPECIFIED("userSpecifiedSeverity"),
        REASON_CALLBACK_SPECIFIED("userCallbackSetSeverity"),
        REASON_UNHANDLED_EXCEPTION_MIDDLEWARE("unhandledExceptionMiddleware"),
        REASON_EXCEPTION_CLASS("exceptionClass");

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
    private final Map<String, String> severityReasonAttributes;
    private final Severity originalSeverity;
    private final boolean unhandled;

    private Severity currentSeverity;

    static HandledState newInstance(SeverityReasonType severityReasonType) {
        return newInstance(severityReasonType, Collections.<String, String>emptyMap(), null, false);
    }

    static HandledState newInstance(SeverityReasonType severityReasonType,
                                    Map<String, String> severityReasonAttributes) {
        return newInstance(severityReasonType, severityReasonAttributes, null, false);
    }

    static HandledState newInstance(SeverityReasonType severityReason, Severity severity) {
        return newInstance(severityReason, Collections.<String, String>emptyMap(), severity, false);
    }

    static HandledState newInstance(SeverityReasonType severityReasonType,
                                    Map<String, String> severityReasonAttributes,
                                    Severity severity,
                                    boolean unhandled) {
        switch (severityReasonType) {
            case REASON_UNHANDLED_EXCEPTION:
            case REASON_UNHANDLED_EXCEPTION_MIDDLEWARE:
                return new HandledState(
                        severityReasonType, severityReasonAttributes, Severity.ERROR, true);
            case REASON_HANDLED_EXCEPTION:
                return new HandledState(
                        severityReasonType, severityReasonAttributes, Severity.WARNING, false);
            case REASON_USER_SPECIFIED:
                return new HandledState(
                        severityReasonType, severityReasonAttributes, severity, false);
            case REASON_EXCEPTION_CLASS:
                return new HandledState(
                        severityReasonType, severityReasonAttributes, severity, unhandled);
            default:
                throw new IllegalArgumentException("Invalid arg for reason: " + severityReasonType);
        }
    }

    private HandledState(SeverityReasonType severityReasonType,
                         Map<String, String> severityReasonAttributes,
                         Severity currentSeverity,
                         boolean unhandled) {
        this.severityReasonType = severityReasonType;
        this.severityReasonAttributes = severityReasonAttributes;
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
        return originalSeverity == currentSeverity
                ? severityReasonType
                : SeverityReasonType.REASON_CALLBACK_SPECIFIED;
    }

    Map<String, String> getSeverityReasonAttributes() {
        return Collections.unmodifiableMap(severityReasonAttributes);
    }

    Severity getCurrentSeverity() {
        return currentSeverity;
    }

    void setCurrentSeverity(Severity severity) {
        this.currentSeverity = severity;
    }

}
