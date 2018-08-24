package com.bugsnag.callbacks;

import com.bugsnag.Report;
import com.bugsnag.servlet.BugsnagServletRequestListener;
import com.bugsnag.util.RequestUtils;

import javax.servlet.http.HttpServletRequest;

public class ServletCallback implements Callback {

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
        report.addToTab("request", RequestUtils.getRequestMetadata(request));

        // Set default context
        if (report.getContext() == null) {
            report.setContext(RequestUtils.generateContext(request));
        }
    }
}
