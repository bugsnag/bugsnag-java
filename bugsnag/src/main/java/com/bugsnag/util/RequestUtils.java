package com.bugsnag.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class RequestUtils {

    private static final String HEADER_X_FORWARDED_FOR = "X-FORWARDED-FOR";

    /**
     * @return A map of metadata from a request
     */
    public static Map<String, Object> getRequestMetadata(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("url", request.getRequestURL().toString());
        map.put("method", request.getMethod());
        map.put("params", new HashMap<String, String[]>(request.getParameterMap()));
        map.put("clientIp", RequestUtils.getClientIp(request));
        map.put("headers", RequestUtils.getHeaderMap(request));
        return map;
    }

    /**
     * @return Create some context for the request, i.e. HTTP method and URI
     */
    public static String generateContext(HttpServletRequest request) {
        return request.getMethod() + " " + request.getRequestURI();
    }

    /**
     * @return The client IP from the request
     */
    private static String getClientIp(HttpServletRequest request) {
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

    /**
     * @return All the headers from the request
     */
    private static Map<String, String> getHeaderMap(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames != null && headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            map.put(key, request.getHeader(key));
        }

        return map;
    }
}
