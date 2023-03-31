package com.bugsnag.callbacks;

import com.bugsnag.Report;
import com.bugsnag.filters.BugsnagContainerRequestFilter;

import org.jboss.resteasy.spi.HttpRequest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JaxrsCallback implements Callback {
    private static final String HEADER_X_FORWARDED_FOR = "X-FORWARDED-FOR";

    /**
     * @return true if the servlet request listener is available.
     */
    public static boolean isAvailable() {
        try {
            Class.forName("javax.ws.rs.container.ContainerRequestFilter", false,
                    JaxrsCallback.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    @Override
    public void beforeNotify(Report report) {
        // Check if we have any servlet request data available
        HttpRequest request = BugsnagContainerRequestFilter.getRequest();

        if (request == null) {
            return;
        }

        // Add request information to metaData
        report
                .addToTab("request", "url", request.getUri().getRequestUri().toString())
                .addToTab("request", "method", request.getHttpMethod())
                .addToTab("request", "params", request.getFormParameters())
                .addToTab("request", "clientIp", getClientIp(request))
                .addToTab("request", "headers", getHeaderMap(request));

        // Set default context
        if (report.getContext() == null) {
            report.setContext(request.getHttpMethod() + " " + request.getUri().getRequestUri());
        }

        // Clear servlet request data
        BugsnagContainerRequestFilter.clearRequest();
    }

    private String getClientIp(HttpRequest request) {
        String remoteAddr = request.getRemoteAddress();
        String forwardedAddr = request.getHttpHeaders().getHeaderString(HEADER_X_FORWARDED_FOR);
        if (forwardedAddr != null) {
            remoteAddr = forwardedAddr;
            int idx = remoteAddr.indexOf(',');
            if (idx > -1) {
                remoteAddr = remoteAddr.substring(0, idx);
            }
        }
        return remoteAddr;
    }

    private Map<String, String> getHeaderMap(HttpRequest request) {
        Map<String, String> headers = new HashMap<String, String>();
        Set<Map.Entry<String, List<String>>> headerNames = request.getMutableHeaders().entrySet();
        for (Map.Entry<String, List<String>> header : headerNames) {
            Iterator<String> headerValues = header.getValue().iterator();
            StringBuilder value = new StringBuilder();

            if (headerValues.hasNext()) {
                value.append(headerValues.next());

                // If there are multiple values for the header, do comma-separated concat
                // as per RFC 2616:
                // https://www.w3.org/Protocols/rfc2616/rfc2616-sec4.html#sec4.2
                while (headerValues.hasNext()) {
                    value.append(",").append(headerValues.next());
                }
            }

            headers.put(header.getKey(), value.toString());
        }

        return headers;
    }
}
