package com.bugsnag;

import com.bugsnag.callbacks.Callback;

import com.bugsnag.servlet.BugsnagServletRequestListener;
import com.bugsnag.util.RequestUtils;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class RequestMetadataInterceptor
        extends HandlerInterceptorAdapter implements Callback {
    private static final String HEADER_X_FORWARDED_FOR = "X-FORWARDED-FOR";

    private static final ThreadLocal<Map<String, Object>> REQUEST_METADATA =
            new ThreadLocal<Map<String, Object>>();

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        if (BugsnagServletRequestListener.getServletRequest() == null) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("url", request.getRequestURL().toString());
            map.put("method", request.getMethod());
            map.put("params", request.getParameterMap());
            map.put("clientIp", RequestUtils.getClientIp(request));
            map.put("headers", RequestUtils.getHeaderMap(request));
            map.put("context", request.getMethod() + " " + request.getRequestURI());

            REQUEST_METADATA.set(map);
        }
        return true;
    }

    @Override
    public void beforeNotify(Report report) {
        Map<String, Object> map = REQUEST_METADATA.get();
        if (map != null) {
            report.addToTab("request", "url", map.get("url"));
            report.addToTab("request", "method", map.get("method"));
            report.addToTab("request", "params", map.get("params"));
            report.addToTab("request", "clientIp", map.get("clientIp"));
            report.addToTab("request", "headers", map.get("headers"));

            // Set default context
            if (report.getContext() == null) {
                report.setContext(map.get("context").toString());
            }
        }
    }
}
