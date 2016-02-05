package com.bugsnag.http;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.io.InputStream;

import org.json.JSONObject;

import com.bugsnag.Configuration;

public class HttpClient {

    private Configuration config;

    public HttpClient(Configuration config) {
        this.config = config;
    }

    public void post(String url, InputStream stream) throws NetworkException {
        post(url, stream, "application/json");
    }

    public void post(String url, JSONObject payload) throws NetworkException, UnsupportedEncodingException {
        post(url, payload.toString(), "application/json");
    }

    public void post(String url, String payload, String contentType) throws NetworkException, UnsupportedEncodingException {
        post(url, new ByteArrayInputStream(payload.getBytes("UTF-8")), contentType);
    }

    public void post(String urlString, InputStream payload, String contentType) throws NetworkException {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(urlString);
            conn = config.getProxy() != null ?
                    (HttpURLConnection) url.openConnection(config.getProxy()) :
                    (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(config.getConnectionTimeout());
            conn.setReadTimeout(config.getReadTimeout());
            conn.setDoOutput(true);
            conn.setChunkedStreamingMode(0);

            // Set the content type header
            if(contentType != null) {
                conn.addRequestProperty("Content-Type", contentType);
            }

            OutputStream out = null;
            try {
                out = conn.getOutputStream();

                // Send request headers and body
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = payload.read(buffer)) != -1)
                {
                    out.write(buffer, 0, bytesRead);
                }
            } finally {
                if(out != null) {
                    out.close();
                }
            }

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
}
