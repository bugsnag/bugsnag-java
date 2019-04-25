package com.bugsnag;

interface BeforeSendSession {
    void beforeSendSession(SessionPayload payload);
}
