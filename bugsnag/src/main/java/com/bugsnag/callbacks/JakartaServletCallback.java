package com.bugsnag.callbacks;

import com.bugsnag.Event;
import com.bugsnag.servlet.jakarta.BugsnagServletRequestListener;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class JakartaServletCallback implements Callback {
    private static final String HEADER_X_FORWARDED_FOR = "X-FORWARDED-FOR";

    /**
     * @return true if the servlet request listener is available.
     */
    public static boolean isAvailable() {
        try {
            Class.forName("jakarta.servlet.ServletRequestListener", false,
                    JakartaServletCallback.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    @Override
    public boolean onError(Event event) {
        // Check if we have any servlet request data available
        HttpServletRequest request = BugsnagServletRequestListener.getServletRequest();
        if (request == null) {
            return true; // nothing to add, but do not cancel
        }

        // Add request information to metadata
        event
                .addMetadata("request", "url", request.getRequestURL().toString())
                .addMetadata("request", "method", request.getMethod())
                .addMetadata("request", "params",
                        new HashMap<String, String[]>(request.getParameterMap()))
                .addMetadata("request", "clientIp", getClientIp(request))
                .addMetadata("request", "headers", getHeaderMap(request));

        // Set default context
        if (event.getContext() == null) {
            event.setContext(request.getMethod() + " " + request.getRequestURI());
        }
        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String remoteAddr = request.getRemoteAddr();
        String forwardedAddr = request.getHeader(HEADER_X_FORWARDED_FOR);
        if (forwardedAddr != null) {
            remoteAddr = forwardedAddr;
            int idx = remoteAddr.indexOf(',');
            if (idx > -1) {
                remoteAddr = remoteAddr.substring(0, idx);
            }
        }
        return remoteAddr;
    }

    private Map<String, String> getHeaderMap(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<String, String>();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames != null && headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            Enumeration<String> headerValues = request.getHeaders(key);
            StringBuilder value = new StringBuilder();

            if (headerValues != null && headerValues.hasMoreElements()) {
                value.append(headerValues.nextElement());

                // If there are multiple values for the header, do comma-separated concat
                // as per RFC 2616:
                // https://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.2
                while (headerValues.hasMoreElements()) {
                    value.append(",").append(headerValues.nextElement());
                }
            }

            headers.put(key, value.toString());
        }

        return headers;
    }
}
