package com.bugsnag.example.servlet;

import com.bugsnag.Bugsnag;
import com.bugsnag.Severity;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ExampleServlet extends HttpServlet {

    private static final long serialVersionUID = 1432171052111530587L;

    private Bugsnag bugsnag;

    /**
     * Simple servlet example
     */
    public ExampleServlet() {
        bugsnag = new Bugsnag("YOUR-API-KEY");
        bugsnag.setProjectPackages("com.bugsnag.example");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
        // Send a handled exception to Bugsnag
        try {
            throw new RuntimeException("Handled exception - default severity");
        } catch (RuntimeException e) {
            bugsnag.notify(e);
        }

        // Send a handled exception to Bugsnag with info severity
        try {
            throw new RuntimeException("Handled exception - INFO severity");
        } catch (RuntimeException ex) {
            bugsnag.notify(ex, Severity.INFO);
        }

        // Throw an exception - not automatically reported so must be handled by the error handler
        throw new ServletException("Servlet exception");
    }
}
