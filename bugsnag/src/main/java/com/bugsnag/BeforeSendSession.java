package com.bugsnag;

interface BeforeSendSession {
    void onSession(SessionPayload payload);
}
