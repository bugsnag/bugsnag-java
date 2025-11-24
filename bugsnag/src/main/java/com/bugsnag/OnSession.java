package com.bugsnag;

@FunctionalInterface
interface OnSession {
    boolean onSession(SessionPayload payload);
}
