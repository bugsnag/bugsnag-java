package com.bugsnag.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient {
    public static void post(String urlString, String payload, String contentType) throws NetworkException {
        post(urlString, stringToByteArray(payload), contentType);
    }

    public static void post(String urlString, byte[] payload, String contentType) throws NetworkException {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true); 
            conn.setFixedLengthStreamingMode(payload.length);

            // Set the content type header
            if(contentType != null) {
                conn.addRequestProperty("Content-Type", contentType);
            }

            // Send request headers and body
            conn.getOutputStream().write(payload);

            // End the request, get the response code
            int status = conn.getResponseCode();
            if(status / 100 != 2) {
                throw new BadResponseException(urlString, status);
            }
        } catch (IOException e) {
            throw new NetworkException(String.format("Network error when posting to %s", urlString), e);
        } finally {
            if(conn != null) {
                conn.disconnect();
            }
        }
    }

    public static byte[] stringToByteArray(String str) {
        byte[] bytes = null;

        try {
            bytes = str.getBytes("UTF-8");
        } catch(UnsupportedEncodingException e) {
            e.printStackTrace(System.err);
        }

        return bytes;
    }
}