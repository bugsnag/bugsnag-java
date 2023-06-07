package com.bugsnag.servlet.jakarta;

import com.bugsnag.Bugsnag;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletRequestEvent;
import jakarta.servlet.ServletRequestListener;
import jakarta.servlet.http.HttpServletRequest;

public class BugsnagServletRequestListener implements ServletRequestListener {

    private static final ThreadLocal<HttpServletRequest> SERVLET_REQUEST =
            new ThreadLocal<HttpServletRequest>();

    public static HttpServletRequest getServletRequest() {
        return SERVLET_REQUEST.get();
    }

    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        trackServletSession();
        ServletRequest servletRequest = servletRequestEvent.getServletRequest();

        if (servletRequest instanceof HttpServletRequest) {
            SERVLET_REQUEST.set((HttpServletRequest) servletRequest);
        }
    }

    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
        SERVLET_REQUEST.remove();
        Bugsnag.clearThreadMetaData();
    }

    private void trackServletSession() {
        for (Bugsnag bugsnag : Bugsnag.uncaughtExceptionClients()) {
            if (bugsnag.shouldAutoCaptureSessions()) {
                bugsnag.startSession();
            }
        }
    }
}
