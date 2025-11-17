package com.bugsnag;

@FunctionalInterface
interface OnSession {
    Boolean onSession(SessionPayload payload);
}
