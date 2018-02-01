package com.bugsnag.example.servlet;

import com.bugsnag.Bugsnag;
import com.bugsnag.Severity;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ExampleServlet extends HttpServlet {
    private Bugsnag bugsnag;

    /**
     * Simple servlet example
     */
    public ExampleServlet() {
        bugsnag = new Bugsnag("YOUR-API-KEY");
        bugsnag.setProjectPackages("com.bugsnag.example");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        // Send a handled exception to Bugsnag
//        try {
//            throw new RuntimeException("Handled exception - default severity");
//        } catch (RuntimeException e) {
//            bugsnag.notify(e);
//        }
//
//        // Send a handled exception to Bugsnag with info severity
//        try {
//            throw new RuntimeException("Handled exception - INFO severity");
//        } catch (RuntimeException ex) {
//            bugsnag.notify(ex, Severity.INFO);
//        }
//
//        // Throw an exception - not automatically reported so must be handled by the error handler
//        throw new ServletException("Servlet exception");
    }
}
