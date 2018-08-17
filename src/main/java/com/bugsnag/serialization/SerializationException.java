package com.bugsnag.serialization;

public class SerializationException extends Exception {
    private static final long serialVersionUID = -6782171186575335048L;

    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
