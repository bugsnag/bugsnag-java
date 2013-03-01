package com.bugsnag.http;

public class BadResponseException extends RuntimeException {
    private int responseCode;
    private String url;

    public BadResponseException(String url, int responseCode) {
        this.url = url;
        this.responseCode = responseCode;
    }

    @Override
    public String getMessage() {
        return String.format("Got non-200 response code (%d) from %s", responseCode, url);
    }

    public int getResponseCode() {
        return responseCode;
    }
}