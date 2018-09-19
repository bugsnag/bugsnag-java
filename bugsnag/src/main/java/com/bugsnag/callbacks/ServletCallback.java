package com.bugsnag.callbacks;

import com.bugsnag.Report;
import com.bugsnag.servlet.BugsnagServletRequestListener;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

public class ServletCallback implements Callback {
    private static final String HEADER_X_FORWARDED_FOR = "X-FORWARDED-FOR";

    /**
     * @return true if the servlet request listener is available.
     */
    public static boolean isAvailable() {
        try {
            Class.forName("javax.servlet.ServletRequestListener", false,
                    ServletCallback.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    @Override
    public void beforeNotify(Report report) {
        // Check if we have any servlet request data available
        HttpServletRequest request = BugsnagServletRequestListener.getServletRequest();
        if (request == null) {
            return;
        }

        // Add request information to metaData
        report
                .addToTab("request", "url", request.getRequestURL().toString())
                .addToTab("request", "method", request.getMethod())
                .addToTab("request", "params",
                        new HashMap<String, String[]>(request.getParameterMap()))
                .addToTab("request", "clientIp", getClientIp(request))
                .addToTab("request", "headers", getHeaderMap(request));

        // Set default context
        if (report.getContext() == null) {
            report.setContext(request.getMethod() + " " + request.getRequestURI());
        }
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
