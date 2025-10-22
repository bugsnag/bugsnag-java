package com.bugsnag;

/**
 * @deprecated Use {@link OnSession} instead
 */
@Deprecated
interface BeforeSendSession extends OnSession {
    @Deprecated
    void beforeSendSession(SessionPayload payload);

    @Override
    default void onSession(SessionPayload payload) {
        beforeSendSession(payload);
    }
}
