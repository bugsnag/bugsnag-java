package com.bugsnag.servlet;

import com.bugsnag.Bugsnag;

import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

import java.lang.Thread.UncaughtExceptionHandler;

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
    }

    private void trackServletSession() {
        for (Bugsnag bugsnag : Bugsnag.uncaughtExceptionClients()) {
            if (bugsnag.shouldAutoCaptureSessions()) {
                bugsnag.startSession();
            }
        }
    }
}
