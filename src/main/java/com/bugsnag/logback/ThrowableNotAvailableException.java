package com.bugsnag.logback;

/** Used to report errors without an attached throwable to Bugsnag. */
class ThrowableNotAvailableException extends Exception {
    ThrowableNotAvailableException(String message) {
        super(message);
    }
}
