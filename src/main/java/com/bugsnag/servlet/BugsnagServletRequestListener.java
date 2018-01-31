package com.bugsnag.servlet;


import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;

public class BugsnagServletRequestListener implements ServletRequestListener {

    private static final ThreadLocal<HttpServletRequest> SERVLET_REQUEST =
            new ThreadLocal<HttpServletRequest>();

    //    private SessionTracker sessionTracker;
    //    private Configuration configuration;
    //
    //    BugsnagServletRequestListener(SessionTracker sessionTracker,
    // Configuration configuration) {
    //        this.sessionTracker = sessionTracker;
    //        this.configuration = configuration;
    //    }

    public static HttpServletRequest getServletRequest() {
        return SERVLET_REQUEST.get();
    }

    @Override
    public void requestInitialized(ServletRequestEvent servletRequestEvent) {
        //        if (configuration.shouldAutoCaptureSessions()) {
        //            logger.warn("Auto-capturing request");
        //            sessionTracker.startNewSession(new Date(), true);
        //        }

        ServletRequest servletRequest = servletRequestEvent.getServletRequest();

        if (servletRequest instanceof HttpServletRequest) {
            SERVLET_REQUEST.set((HttpServletRequest) servletRequest);
        }
    }

    @Override
    public void requestDestroyed(ServletRequestEvent servletRequestEvent) {
        SERVLET_REQUEST.remove();
    }
}
